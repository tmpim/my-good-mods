package pw.tmpim.goodflags.client

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.item.DyeItem
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import pw.tmpim.goodflags.GoodFlags.MOD_ID
import pw.tmpim.goodflags.block.FlagBlockEntity
import pw.tmpim.goodflags.block.FlagSpec.FLAG_HEIGHT
import pw.tmpim.goodflags.block.FlagSpec.FLAG_WIDTH
import pw.tmpim.goodflags.block.FlagSpec.getGLColor
import pw.tmpim.goodflags.data.TranslationString
import pw.tmpim.goodflags.net.FlagNetworkingC2S
import pw.tmpim.goodutils.i18n.i18n
import pw.tmpim.goodutils.net.sendToServer
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sqrt

private const val TC = TranslationString.COLOR
private const val TT = TranslationString.TOOL

@Environment(EnvType.CLIENT)
class FlagPaintScreen(private val flagEntity: FlagBlockEntity) : Screen() {
  companion object {
    // Button IDs
    private const val BTN_DONE = 0
    private const val BTN_CANCEL = 1

    private const val MAX_UNDO = 32
  }

  private enum class Tool(val labelKey: String) {
    PENCIL("$TT.pencil"),
    FILL("$TT.fill"),
    ERASER("$TT.eraser"),
    LINE("$TT.line"),
    RECT("$TT.rect"),
    CIRCLE("$TT.circle")
  }

  private var selectedColor = 0 // Default to black
  private var currentTool = Tool.PENCIL
  private var brushSize = 1 // 1, 2, 3, or 4

  private val localPixels = flagEntity.pixels.copyOf()

  // Undo / redo stacks — each entry is a full copy of localPixels
  private val undoStack = ArrayDeque<ByteArray>(MAX_UNDO)
  private val redoStack = ArrayDeque<ByteArray>(MAX_UNDO)

  // Canvas layout
  private val pixelScale = 4
  private val canvasWidth = FLAG_WIDTH * pixelScale    // 192
  private val canvasHeight = FLAG_HEIGHT * pixelScale  // 128
  private var canvasX = 0
  private var canvasY = 0

  // Palette layout
  private val paletteSwatchSize = 14
  private val paletteGap = 2
  private var paletteX = 0
  private var paletteY = 0

  // Tool buttons (drawn manually so we can show selection state)
  private val toolBtnW = 46
  private val toolBtnH = 16
  private val toolBtnGap = 4
  private var toolsX = 0
  private var toolsY = 0

  // Brush size buttons (drawn manually, to the right of the canvas)
  private val brushBtnSize = 14
  private val brushBtnGap = 4
  private var brushX = 0
  private var brushY = 0

  // Stroke / shape state
  private var painting = false
  private var lastPx = -1
  private var lastPy = -1
  // Start point for LINE, RECT, and CIRCLE tools
  private var shapeStartX = -1
  private var shapeStartY = -1
  // Snapshot taken at mouse-down for shape tools (so undo captures the pre-stroke state)
  private var strokeSnapshot: ByteArray? = null

  private val tr by ::textRenderer

  override fun init() {
    buttons.clear()

    // Center canvas
    canvasX = (width - canvasWidth) / 2
    canvasY = (height - canvasHeight) / 2 - 23

    // Palette below canvas
    val totalPaletteWidth = 16 * (paletteSwatchSize + paletteGap) - paletteGap
    paletteX = (width - totalPaletteWidth) / 2
    paletteY = canvasY + canvasHeight + 15

    // Tool buttons to the left of the canvas, vertically centered on it
    toolsX = canvasX - toolBtnW - 18
    val totalToolsH = Tool.entries.size * (toolBtnH + toolBtnGap) - toolBtnGap
    toolsY = canvasY + (canvasHeight - totalToolsH) / 2

    // Brush size buttons to the right of the canvas, near the top
    brushX = canvasX + canvasWidth + 18
    brushY = toolsY

    val btnY = paletteY + paletteSwatchSize + 10
    buttons.add(ButtonWidget(BTN_DONE,   width / 2 - 105, btnY, 100, 20, "gui.done".i18n()))
    buttons.add(ButtonWidget(BTN_CANCEL, width / 2 + 5,   btnY, 100, 20, "gui.cancel".i18n()))
  }

  override fun buttonClicked(button: ButtonWidget) {
    if (!button.active) return
    when (button.id) {
      BTN_DONE   -> {
        System.arraycopy(localPixels, 0, flagEntity.pixels, 0, localPixels.size)
        flagEntity.dirty = true
        FlagNetworkingC2S.createFlagUpdatePacket(flagEntity.x, flagEntity.y, flagEntity.z, localPixels)
          .sendToServer()
      }
      BTN_CANCEL -> {}
    }

    minecraft.setScreen(null)
  }

  override fun keyPressed(character: Char, keyCode: Int) {
    val ctrl = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)
    val shift = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)
    when {
      ctrl && keyCode == Keyboard.KEY_Z -> if (shift) redo() else undo()
      ctrl && keyCode == Keyboard.KEY_Y -> redo()
      else -> super.keyPressed(character, keyCode)
    }
  }

  // ---------------------------------------------------------------------------
  // Undo / redo
  // ---------------------------------------------------------------------------

  /** Push current pixels onto the undo stack, clear redo stack. */
  private fun pushUndo(snapshot: ByteArray = localPixels.copyOf()) {
    if (undoStack.size >= MAX_UNDO) undoStack.removeFirst()
    undoStack.addLast(snapshot)
    redoStack.clear()
  }

  private fun undo() {
    if (undoStack.isEmpty()) return
    redoStack.addLast(localPixels.copyOf())
    val prev = undoStack.removeLast()
    System.arraycopy(prev, 0, localPixels, 0, localPixels.size)
  }

  private fun redo() {
    if (redoStack.isEmpty()) return
    undoStack.addLast(localPixels.copyOf())
    val next = redoStack.removeLast()
    System.arraycopy(next, 0, localPixels, 0, localPixels.size)
  }

  override fun mouseClicked(mouseX: Int, mouseY: Int, button: Int) {
    super.mouseClicked(mouseX, mouseY, button)
    if (button != 0) return

    // Tool button clicks
    for ((i, tool) in Tool.entries.withIndex()) {
      val bx = toolsX
      val by = toolsY + i * (toolBtnH + toolBtnGap)
      if (mouseX in bx until bx + toolBtnW && mouseY in by until by + toolBtnH) {
        currentTool = tool
        return
      }
    }

    // Brush size button clicks
    for (idx in 0 until 4) {
      val bx = brushX
      val by = brushY + idx * (brushBtnSize + brushBtnGap)
      if (mouseX in bx until bx + brushBtnSize && mouseY in by until by + brushBtnSize) {
        brushSize = idx + 1
        return
      }
    }

    // Palette clicks
    for (i in 0 until 16) {
      val sx = paletteX + i * (paletteSwatchSize + paletteGap)
      val sy = paletteY
      if (mouseX in sx until sx + paletteSwatchSize && mouseY in sy until sy + paletteSwatchSize) {
        selectedColor = i
        return
      }
    }

    // Canvas clicks
    if (mouseX in canvasX until canvasX + canvasWidth && mouseY in canvasY until canvasY + canvasHeight) {
      val px = (mouseX - canvasX) / pixelScale
      val py = (mouseY - canvasY) / pixelScale
      when (currentTool) {
        Tool.FILL -> {
          pushUndo()
          floodFill(px, py, selectedColor)
        }
        Tool.LINE, Tool.RECT, Tool.CIRCLE -> {
          strokeSnapshot = localPixels.copyOf()
          painting = true
          shapeStartX = px
          shapeStartY = py
        }
        Tool.PENCIL, Tool.ERASER -> {
          strokeSnapshot = localPixels.copyOf()
          painting = true
          lastPx = px
          lastPy = py
          paintPixelBrushed(px, py)
        }
      }
    }
  }

  override fun mouseReleased(mouseX: Int, mouseY: Int, button: Int) {
    super.mouseReleased(mouseX, mouseY, button)
    if (button == 0) {
      if (painting) {
        val px = ((mouseX - canvasX) / pixelScale).coerceIn(0, FLAG_WIDTH - 1)
        val py = ((mouseY - canvasY) / pixelScale).coerceIn(0, FLAG_HEIGHT - 1)
        when (currentTool) {
          Tool.LINE -> if (shapeStartX >= 0 && shapeStartY >= 0) {
            val snap = strokeSnapshot
            if (snap != null) pushUndo(snap)
            paintStrokeBrushed(shapeStartX, shapeStartY, px, py)
          }
          Tool.RECT -> if (shapeStartX >= 0 && shapeStartY >= 0) {
            val snap = strokeSnapshot
            if (snap != null) pushUndo(snap)
            paintRect(shapeStartX, shapeStartY, px, py)
          }
          Tool.CIRCLE -> if (shapeStartX >= 0 && shapeStartY >= 0) {
            val snap = strokeSnapshot
            if (snap != null) pushUndo(snap)
            paintCircle(shapeStartX, shapeStartY, px, py)
          }
          Tool.PENCIL, Tool.ERASER -> {
            // Finish the stroke: push the pre-stroke snapshot
            val snap = strokeSnapshot
            if (snap != null) pushUndo(snap)
          }
          else -> {}
        }
      }
      painting = false
      lastPx = -1
      lastPy = -1
      shapeStartX = -1
      shapeStartY = -1
      strokeSnapshot = null
    }
  }

  // ---------------------------------------------------------------------------
  // Drawing helpers
  // ---------------------------------------------------------------------------

  private fun paintPixelBrushed(px: Int, py: Int) {
    val color = if (currentTool == Tool.ERASER) 15 else selectedColor
    val half = brushSize / 2
    for (dy in -half until brushSize - half) {
      for (dx in -half until brushSize - half) {
        val nx = px + dx
        val ny = py + dy
        if (nx in 0 until FLAG_WIDTH && ny in 0 until FLAG_HEIGHT) {
          localPixels[ny * FLAG_WIDTH + nx] = (color and 0xF).toByte()
        }
      }
    }
  }

  /** Bresenham with brush size applied at each point. */
  private fun paintStrokeBrushed(x0: Int, y0: Int, x1: Int, y1: Int) {
    val color = if (currentTool == Tool.ERASER) 15 else selectedColor
    val half = brushSize / 2
    bresenham(x0, y0, x1, y1) { cx, cy ->
      for (dy in -half until brushSize - half) {
        for (dx in -half until brushSize - half) {
          val nx = cx + dx
          val ny = cy + dy
          if (nx in 0 until FLAG_WIDTH && ny in 0 until FLAG_HEIGHT) {
            localPixels[ny * FLAG_WIDTH + nx] = (color and 0xF).toByte()
          }
        }
      }
    }
  }

  private fun paintRect(x0: Int, y0: Int, x1: Int, y1: Int) {
    val minX = minOf(x0, x1)
    val maxX = maxOf(x0, x1)
    val minY = minOf(y0, y1)
    val maxY = maxOf(y0, y1)
    val color = if (currentTool == Tool.ERASER) 15 else selectedColor
    for (py in minY..maxY) {
      for (px in minX..maxX) {
        if (px in 0 until FLAG_WIDTH && py in 0 until FLAG_HEIGHT) {
          localPixels[py * FLAG_WIDTH + px] = (color and 0xF).toByte()
        }
      }
    }
  }

  /**
   * Midpoint circle algorithm. Draws a filled circle whose bounding box is defined
   * by the drag rectangle (start → end). The radius is half the shorter side.
   */
  private fun paintCircle(x0: Int, y0: Int, x1: Int, y1: Int) {
    val color = if (currentTool == Tool.ERASER) 15 else selectedColor
    val cx = (x0 + x1) / 2.0
    val cy = (y0 + y1) / 2.0
    val rx = abs(x1 - x0) / 2.0
    val ry = abs(y1 - y0) / 2.0
    val r = minOf(rx, ry)
    circlePixels(cx, cy, r) { px, py ->
      if (px in 0 until FLAG_WIDTH && py in 0 until FLAG_HEIGHT) {
        localPixels[py * FLAG_WIDTH + px] = (color and 0xF).toByte()
      }
    }
  }

  /**
   * Iterate all pixels inside (inclusive) the circle centered at (cx, cy) with radius r.
   * Uses a scanline approach for a filled disc.
   */
  private inline fun circlePixels(cx: Double, cy: Double, r: Double, plot: (Int, Int) -> Unit) {
    if (r < 0.5) {
      plot(cx.roundToInt(), cy.roundToInt())
      return
    }
    val minY = (cy - r).toInt()
    val maxY = (cy + r).toInt()
    for (py in minY..maxY) {
      val dy = py + 0.5 - cy
      val dx = sqrt(maxOf(0.0, r * r - dy * dy))
      val minX = (cx - dx).toInt()
      val maxX = (cx + dx).toInt()
      for (px in minX..maxX) {
        plot(px, py)
      }
    }
  }

  private inline fun bresenham(x0: Int, y0: Int, x1: Int, y1: Int, plot: (Int, Int) -> Unit) {
    val dx = abs(x1 - x0)
    val dy = abs(y1 - y0)
    val sx = if (x0 < x1) 1 else -1
    val sy = if (y0 < y1) 1 else -1
    var err = dx - dy
    var cx = x0
    var cy = y0
    while (true) {
      plot(cx, cy)
      if (cx == x1 && cy == y1) break
      val e2 = 2 * err
      if (e2 > -dy) { err -= dy; cx += sx }
      if (e2 < dx)  { err += dx; cy += sy }
    }
  }

  /** 4-connected flood fill. */
  private fun floodFill(startX: Int, startY: Int, fillColor: Int) {
    if (startX !in 0 until FLAG_WIDTH || startY !in 0 until FLAG_HEIGHT) return
    val target = localPixels[startY * FLAG_WIDTH + startX].toInt() and 0xF
    val fill = fillColor and 0xF
    if (target == fill) return

    val queue = ArrayDeque<Int>()
    queue.add(startY * FLAG_WIDTH + startX)
    while (queue.isNotEmpty()) {
      val idx = queue.removeFirst()
      if (localPixels[idx].toInt() and 0xF != target) continue
      localPixels[idx] = fill.toByte()
      val x = idx % FLAG_WIDTH
      val y = idx / FLAG_WIDTH
      if (x + 1 < FLAG_WIDTH)  queue.add(idx + 1)
      if (x - 1 >= 0)          queue.add(idx - 1)
      if (y + 1 < FLAG_HEIGHT) queue.add(idx + FLAG_WIDTH)
      if (y - 1 >= 0)          queue.add(idx - FLAG_WIDTH)
    }
  }

  // ---------------------------------------------------------------------------
  // Rendering
  // ---------------------------------------------------------------------------

  override fun render(mouseX: Int, mouseY: Int, delta: Float) {
    renderBackground()

    // Continuous pencil/eraser stroke — connect last painted pixel to current
    if (painting && Mouse.isButtonDown(0)) {
      if (currentTool == Tool.PENCIL || currentTool == Tool.ERASER) {
        val px = (mouseX - canvasX) / pixelScale
        val py = (mouseY - canvasY) / pixelScale
        paintStrokeBrushed(lastPx, lastPy, px, py)
        lastPx = px
        lastPy = py
      }
    }

    // Title
    drawCenteredTextWithShadow(tr, "gui.$MOD_ID.title".i18n(), width / 2, canvasY - 15, 0xFFFFFF)

    // Canvas border + pixels
    fill(canvasX - 1, canvasY - 1, canvasX + canvasWidth + 1, canvasY + canvasHeight + 1, 0xFF333333.toInt())
    for (py in 0 until FLAG_HEIGHT) {
      for (px in 0 until FLAG_WIDTH) {
        val colorIndex = localPixels[py * FLAG_WIDTH + px].toInt() and 0xF
        val color = getGLColor(colorIndex)
        val sx = canvasX + px * pixelScale
        val sy = canvasY + py * pixelScale
        fill(sx, sy, sx + pixelScale, sy + pixelScale, color)
      }
    }
    drawGridLines()

    // Line / rect / circle preview overlay (while dragging)
    if (painting && Mouse.isButtonDown(0) &&
        (currentTool == Tool.LINE || currentTool == Tool.RECT || currentTool == Tool.CIRCLE) &&
        shapeStartX >= 0 && shapeStartY >= 0) {
      val endPx = ((mouseX - canvasX) / pixelScale).coerceIn(0, FLAG_WIDTH - 1)
      val endPy = ((mouseY - canvasY) / pixelScale).coerceIn(0, FLAG_HEIGHT - 1)
      drawShapePreview(shapeStartX, shapeStartY, endPx, endPy)
    }

    // Hover indicator (only when mouse is over canvas and not dragging a shape)
    val hoverInCanvas = mouseX in canvasX until canvasX + canvasWidth &&
                        mouseY in canvasY until canvasY + canvasHeight
    if (hoverInCanvas) {
      val hx = (mouseX - canvasX) / pixelScale
      val hy = (mouseY - canvasY) / pixelScale
      val effectiveBrush = if (currentTool == Tool.FILL || currentTool == Tool.RECT || currentTool == Tool.CIRCLE) 1 else brushSize
      val effectiveColor = if (currentTool == Tool.ERASER) 15 else selectedColor
      val half = effectiveBrush / 2
      val sx = canvasX + (hx - half) * pixelScale
      val sy = canvasY + (hy - half) * pixelScale
      val ex = sx + effectiveBrush * pixelScale
      val ey = sy + effectiveBrush * pixelScale
      // Outline in the active color (with alpha)
      val baseColor = getGLColor(effectiveColor)
      val hoverColor = (baseColor and 0x00FFFFFF) or 0xCC000000.toInt()
      fill(sx, sy, ex, sy + 1, hoverColor)
      fill(sx, ey - 1, ex, ey, hoverColor)
      fill(sx, sy, sx + 1, ey, hoverColor)
      fill(ex - 1, sy, ex, ey, hoverColor)
    }

    // Tool buttons (manually drawn for custom selection highlight)
    for ((i, tool) in Tool.entries.withIndex()) {
      val bx = toolsX
      val by = toolsY + i * (toolBtnH + toolBtnGap)
      val selected = tool == currentTool
      val border = if (selected) 0xFFFFFFFF.toInt() else 0xFF666666.toInt()
      val bg     = if (selected) 0xFF3A3A6A.toInt() else 0xFF444444.toInt()
      fill(bx - 1, by - 1, bx + toolBtnW + 1, by + toolBtnH + 1, border)
      fill(bx, by, bx + toolBtnW, by + toolBtnH, bg)
      drawCenteredTextWithShadow(tr, tool.labelKey.i18n(), bx + toolBtnW / 2, by + (toolBtnH - 8) / 2, 0xFFFFFF)
    }

    // "Tools" label above tool buttons
    drawCenteredTextWithShadow(tr, "$TT.title".i18n(), toolsX + toolBtnW / 2, toolsY - 12, 0xAAAAAA)

    // Brush size buttons (right side) — dimmed when Fill is active (fill always uses size 1)
    val brushDisabled = currentTool == Tool.FILL || currentTool == Tool.RECT || currentTool == Tool.CIRCLE
    drawCenteredTextWithShadow(
      tr,
      "$TT.brush".i18n(),
      brushX + brushBtnSize / 2,
      brushY - 12,
      if (brushDisabled) 0x555555 else 0xAAAAAA
    )

    for (idx in 0 until 4) {
      val size = idx + 1
      val bx = brushX
      val by = brushY + idx * (brushBtnSize + brushBtnGap)
      val selected = size == brushSize && !brushDisabled
      val border = if (selected) 0xFFFFFFFF.toInt() else if (brushDisabled) 0xFF444444.toInt() else 0xFF666666.toInt()
      val bg     = if (selected) 0xFF3A3A6A.toInt() else if (brushDisabled) 0xFF2A2A2A.toInt() else 0xFF444444.toInt()
      val textColor = if (brushDisabled) 0x555555 else 0xFFFFFF
      fill(bx - 1, by - 1, bx + brushBtnSize + 1, by + brushBtnSize + 1, border)
      fill(bx, by, bx + brushBtnSize, by + brushBtnSize, bg)
      drawCenteredTextWithShadow(tr, "$size", bx + brushBtnSize / 2, by + (brushBtnSize - 8) / 2, textColor)
    }

    // Palette
    fun swatchPos(i: Int) = paletteX + i * (paletteSwatchSize + paletteGap)

    for (i in 0 until 16) {
      val sx = swatchPos(i)
      val sy = paletteY
      fill(sx - 1, sy - 1, sx + paletteSwatchSize + 1, sy + paletteSwatchSize + 1, 0xFF000000.toInt())
      fill(sx, sy, sx + paletteSwatchSize, sy + paletteSwatchSize, getGLColor(i))
    }

    // Selected color highlight
    run {
      val sx = swatchPos(selectedColor)
      val sy = paletteY
      fill(sx - 2, sy - 2, sx + paletteSwatchSize + 2, sy + paletteSwatchSize + 2, 0xFFFFFFFF.toInt())
      fill(sx - 1, sy - 1, sx + paletteSwatchSize + 1, sy + paletteSwatchSize + 1, 0xFF000000.toInt())
      fill(sx, sy, sx + paletteSwatchSize, sy + paletteSwatchSize, getGLColor(selectedColor))
    }

    // Selected color name
    val colorName = "$TC.${DyeItem.names[selectedColor]}".i18n()
    drawCenteredTextWithShadow(tr, colorName, width / 2, paletteY - 12, 0xAAAAAA)

    super.render(mouseX, mouseY, delta)
  }

  /** Draw a translucent preview of the shape being dragged. */
  private fun drawShapePreview(x0: Int, y0: Int, x1: Int, y1: Int) {
    val baseColor = getGLColor(selectedColor)
    val color = (baseColor and 0x00FFFFFF) or 0x80000000.toInt()
    when (currentTool) {
      Tool.LINE -> {
        val half = brushSize / 2
        val pixels = mutableSetOf<Long>()
        bresenham(x0, y0, x1, y1) { cx, cy ->
          for (dy in -half until brushSize - half)
            for (dx in -half until brushSize - half) {
              val x = cx + dx
              val y = cy + dy
              if (x in 0..<FLAG_WIDTH && y in 0..<FLAG_HEIGHT)
                pixels.add((x.toLong() shl 32) or y.toLong())
            }
        }
        for (key in pixels) {
          val px = (key shr 32).toInt()
          val py = key.toInt()
          val sx = canvasX + px * pixelScale
          val sy = canvasY + py * pixelScale
          fill(sx, sy, sx + pixelScale, sy + pixelScale, color)
        }
      }
      Tool.RECT -> {
        val minX = minOf(x0, x1)
        val maxX = maxOf(x0, x1)
        val minY = minOf(y0, y1)
        val maxY = maxOf(y0, y1)
        val sx = canvasX + minX * pixelScale
        val sy = canvasY + minY * pixelScale
        val ex = canvasX + (maxX + 1) * pixelScale
        val ey = canvasY + (maxY + 1) * pixelScale
        fill(sx, sy, ex, ey, color)
      }
      Tool.CIRCLE -> {
        val cx = (x0 + x1) / 2.0
        val cy = (y0 + y1) / 2.0
        val rx = abs(x1 - x0) / 2.0
        val ry = abs(y1 - y0) / 2.0
        val r = minOf(rx, ry)
        circlePixels(cx, cy, r) { px, py ->
          if (px in 0..<FLAG_WIDTH && py in 0..<FLAG_HEIGHT) {
            val sx = canvasX + px * pixelScale
            val sy = canvasY + py * pixelScale
            fill(sx, sy, sx + pixelScale, sy + pixelScale, color)
          }
        }
      }
      else -> {}
    }
  }

  private fun drawGridLines() {
    val gridColor = 0x20000000
    for (px in 0..FLAG_WIDTH step 4) {
      val sx = canvasX + px * pixelScale
      fill(sx, canvasY, sx + 1, canvasY + canvasHeight, gridColor)
    }
    for (py in 0..FLAG_HEIGHT step 4) {
      val sy = canvasY + py * pixelScale
      fill(canvasX, sy, canvasX + canvasWidth, sy + 1, gridColor)
    }
  }

  override fun shouldPause(): Boolean = false
}
