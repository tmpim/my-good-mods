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
import pw.tmpim.goodflags.client.paint.*
import pw.tmpim.goodflags.client.paint.PanelLayout.Companion.BRUSH_BTN_GAP
import pw.tmpim.goodflags.client.paint.PanelLayout.Companion.BRUSH_BTN_SIZE
import pw.tmpim.goodflags.client.paint.PanelLayout.Companion.FONT_HEIGHT
import pw.tmpim.goodflags.client.paint.PanelLayout.Companion.GRID_STEP
import pw.tmpim.goodflags.client.paint.PanelLayout.Companion.LABEL_OFFSET_Y
import pw.tmpim.goodflags.client.paint.PanelLayout.Companion.MAX_BRUSH_SIZE
import pw.tmpim.goodflags.client.paint.PanelLayout.Companion.PALETTE_BUTTON_GAP
import pw.tmpim.goodflags.client.paint.PanelLayout.Companion.PALETTE_CANVAS_GAP
import pw.tmpim.goodflags.client.paint.PanelLayout.Companion.PALETTE_GAP
import pw.tmpim.goodflags.client.paint.PanelLayout.Companion.PALETTE_SIZE
import pw.tmpim.goodflags.client.paint.PanelLayout.Companion.PALETTE_SWATCH_SIZE
import pw.tmpim.goodflags.client.paint.PanelLayout.Companion.TOOL_BTN_GAP
import pw.tmpim.goodflags.client.paint.PanelLayout.Companion.TOOL_BTN_H
import pw.tmpim.goodflags.client.paint.PanelLayout.Companion.TOOL_BTN_W
import pw.tmpim.goodflags.data.TranslationString
import pw.tmpim.goodflags.net.FlagNetworkingC2S
import pw.tmpim.goodutils.i18n.i18n
import pw.tmpim.goodutils.net.sendToServer

@Environment(EnvType.CLIENT)
class FlagPaintScreen(private val flagEntity: FlagBlockEntity) : Screen() {

  // ── State ─────────────────────────────────────────────────────────────────

  private var selectedColor = 0
  private var currentTool = PaintTool.PENCIL
  private var brushSize = 1

  private val canvas = PixelCanvas(flagEntity.pixels)
  private val stroke = StrokeState()
  private val viewport = CanvasViewport()

  private val effectiveColor: Int
    get() = canvas.resolveColor(selectedColor, currentTool == PaintTool.ERASER)

  private val effectiveBrushSize: Int
    get() = if (currentTool.ignoresBrushSize) 1 else brushSize

  // ── Layout ────────────────────────────────────────────────────────────────

  companion object {
    private const val BTN_DONE = 0
    private const val BTN_CANCEL = 1
    private const val CANVAS_VERTICAL_OFFSET = 23
  }

  private lateinit var layout: PanelLayout

  private val tr by ::textRenderer

  // ── Initialization ────────────────────────────────────────────────────────

  override fun init() {
    buttons.clear()
    viewport.layout(width, height, CANVAS_VERTICAL_OFFSET)
    layout = PanelLayout.compute(width, viewport)
    addActionButtons()
  }

  private fun addActionButtons() {
    val btnY = layout.paletteY + PALETTE_SWATCH_SIZE + PALETTE_BUTTON_GAP
    buttons.add(ButtonWidget(BTN_DONE, width / 2 - 105, btnY, 100, 20, "gui.done".i18n()))
    buttons.add(ButtonWidget(BTN_CANCEL, width / 2 + 5, btnY, 100, 20, "gui.cancel".i18n()))
  }

  // ── Button handling ───────────────────────────────────────────────────────

  override fun buttonClicked(button: ButtonWidget) {
    if (!button.active) return
    if (button.id == BTN_DONE) submitCanvas()
    minecraft.setScreen(null)
  }

  private fun submitCanvas() {
    System.arraycopy(canvas.pixels, 0, flagEntity.pixels, 0, canvas.pixels.size)
    flagEntity.dirty = true
    FlagNetworkingC2S.createFlagUpdatePacket(flagEntity.x, flagEntity.y, flagEntity.z, canvas.pixels)
      .sendToServer()
  }

  // ── Keyboard input ────────────────────────────────────────────────────────

  override fun keyPressed(character: Char, keyCode: Int) {
    val ctrl = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)
    val shift = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)
    when {
      ctrl && keyCode == Keyboard.KEY_Z -> if (shift) canvas.redo() else canvas.undo()
      ctrl && keyCode == Keyboard.KEY_Y -> canvas.redo()
      else -> super.keyPressed(character, keyCode)
    }
  }

  // ── Mouse input ───────────────────────────────────────────────────────────

  override fun mouseClicked(mouseX: Int, mouseY: Int, button: Int) {
    super.mouseClicked(mouseX, mouseY, button)
    if (button != 0) return

    if (handleToolClick(mouseX, mouseY)) return
    if (handleBrushClick(mouseX, mouseY)) return
    if (handlePaletteClick(mouseX, mouseY)) return
    handleCanvasClick(mouseX, mouseY)
  }

  private fun handleToolClick(mouseX: Int, mouseY: Int): Boolean {
    val idx = HitTest.vertical(mouseX, mouseY, layout.toolsX, layout.toolsY, TOOL_BTN_W, TOOL_BTN_H, TOOL_BTN_GAP, PaintTool.entries.size)
    if (idx >= 0) { currentTool = PaintTool.entries[idx]; return true }
    return false
  }

  private fun handleBrushClick(mouseX: Int, mouseY: Int): Boolean {
    val idx = HitTest.vertical(mouseX, mouseY, layout.brushX, layout.brushY, BRUSH_BTN_SIZE, BRUSH_BTN_SIZE, BRUSH_BTN_GAP, MAX_BRUSH_SIZE)
    if (idx >= 0) { brushSize = idx + 1; return true }
    return false
  }

  private fun handlePaletteClick(mouseX: Int, mouseY: Int): Boolean {
    val idx = HitTest.horizontal(mouseX, mouseY, layout.paletteX, layout.paletteY, PALETTE_SWATCH_SIZE, PALETTE_SWATCH_SIZE, PALETTE_GAP, PALETTE_SIZE)
    if (idx >= 0) { selectedColor = idx; return true }
    return false
  }

  private fun handleCanvasClick(mouseX: Int, mouseY: Int) {
    if (!viewport.isInsideCanvas(mouseX, mouseY)) return
    val px = viewport.toPixelX(mouseX)
    val py = viewport.toPixelY(mouseY)
    when {
      currentTool == PaintTool.FILL -> {
        canvas.pushUndo()
        canvas.floodFill(px, py, selectedColor)
      }
      currentTool.isShape -> stroke.beginShape(px, py, canvas.pixels.copyOf())
      currentTool.isFreehand -> {
        stroke.beginFreehand(px, py, canvas.pixels.copyOf())
        canvas.paintBrushed(px, py, effectiveColor, brushSize)
      }
    }
  }

  override fun mouseReleased(mouseX: Int, mouseY: Int, button: Int) {
    super.mouseReleased(mouseX, mouseY, button)
    if (button == 0) {
      if (stroke.active) commitStroke(mouseX, mouseY)
      stroke.reset()
    }
  }

  private fun commitStroke(mouseX: Int, mouseY: Int) {
    stroke.snapshot?.let(canvas::pushUndo)
    if (currentTool.isShape && stroke.hasShapeStart) {
      val px = viewport.toPixelX(mouseX)
      val py = viewport.toPixelY(mouseY)
      canvas.applyShape(currentTool, stroke.shapeStartX, stroke.shapeStartY, px, py, effectiveColor, brushSize)
    }
  }

  // ── Rendering ─────────────────────────────────────────────────────────────

  override fun render(mouseX: Int, mouseY: Int, delta: Float) {
    renderBackground()
    updateContinuousStroke(mouseX, mouseY)

    drawCenteredTextWithShadow(tr, "gui.$MOD_ID.title".i18n(), width / 2, viewport.canvasY - PALETTE_CANVAS_GAP, PaintColors.TEXT)

    drawCanvas()
    drawGridLines()
    drawShapePreview(mouseX, mouseY)
    drawHoverIndicator(mouseX, mouseY)
    drawToolButtons()
    drawBrushSizeButtons()
    drawPalette()

    super.render(mouseX, mouseY, delta)
  }

  private fun updateContinuousStroke(mouseX: Int, mouseY: Int) {
    if (!stroke.active || !Mouse.isButtonDown(0) || !currentTool.isFreehand) return
    val px = viewport.toPixelX(mouseX)
    val py = viewport.toPixelY(mouseY)
    canvas.paintLineBrushed(stroke.lastPx, stroke.lastPy, px, py, effectiveColor, brushSize)
    stroke.updateLastPosition(px, py)
  }

  // ── Canvas rendering ──────────────────────────────────────────────────────

  private fun drawCanvas() {
    val vp = viewport
    fill(vp.canvasX - 1, vp.canvasY - 1, vp.canvasX + vp.canvasWidth + 1, vp.canvasY + vp.canvasHeight + 1, PaintColors.CANVAS_BORDER)
    for (py in 0 until FLAG_HEIGHT) {
      for (px in 0 until FLAG_WIDTH) {
        val colorIndex = canvas.getPixelColor(px, py)
        fill(vp.toScreenX(px), vp.toScreenY(py), vp.toScreenX(px) + vp.scale, vp.toScreenY(py) + vp.scale, getGLColor(colorIndex))
      }
    }
  }

  private fun drawGridLines() {
    val vp = viewport
    for (px in 0..FLAG_WIDTH step GRID_STEP) {
      val sx = vp.toScreenX(px)
      fill(sx, vp.canvasY, sx + 1, vp.canvasY + vp.canvasHeight, PaintColors.GRID)
    }
    for (py in 0..FLAG_HEIGHT step GRID_STEP) {
      val sy = vp.toScreenY(py)
      fill(vp.canvasX, sy, vp.canvasX + vp.canvasWidth, sy + 1, PaintColors.GRID)
    }
  }

  private fun drawShapePreview(mouseX: Int, mouseY: Int) {
    if (!stroke.active || !Mouse.isButtonDown(0) || !currentTool.isShape || !stroke.hasShapeStart) return
    val endPx = viewport.toPixelX(mouseX)
    val endPy = viewport.toPixelY(mouseY)
    val previewColor = (getGLColor(selectedColor) and 0x00FFFFFF) or PaintColors.PREVIEW_ALPHA_MASK
    val previewPixels = canvas.collectShapePixels(currentTool, stroke.shapeStartX, stroke.shapeStartY, endPx, endPy, brushSize)
    renderPixelSet(previewPixels, previewColor)
  }

  private fun drawHoverIndicator(mouseX: Int, mouseY: Int) {
    if (!viewport.isInsideCanvas(mouseX, mouseY)) return
    val hx = viewport.toPixelX(mouseX)
    val hy = viewport.toPixelY(mouseY)
    val brush = effectiveBrushSize
    val half = brush / 2
    val sx = viewport.toScreenX(hx - half)
    val sy = viewport.toScreenY(hy - half)
    val ex = sx + brush * viewport.scale
    val ey = sy + brush * viewport.scale
    val hoverColor = (getGLColor(effectiveColor) and 0x00FFFFFF) or PaintColors.HOVER_ALPHA_MASK
    fill(sx, sy, ex, sy + 1, hoverColor)
    fill(sx, ey - 1, ex, ey, hoverColor)
    fill(sx, sy, sx + 1, ey, hoverColor)
    fill(ex - 1, sy, ex, ey, hoverColor)
  }

  private fun renderPixelSet(pixels: Set<Long>, color: Int) {
    for (key in pixels) {
      val (px, py) = PixelCanvas.unpackCoords(key)
      fill(viewport.toScreenX(px), viewport.toScreenY(py), viewport.toScreenX(px) + viewport.scale, viewport.toScreenY(py) + viewport.scale, color)
    }
  }

  // ── UI panel drawing ──────────────────────────────────────────────────────

  private fun drawLabeledButton(
    x: Int, y: Int, w: Int, h: Int,
    label: String,
    borderColor: Int, bgColor: Int, textColor: Int,
  ) {
    fill(x - 1, y - 1, x + w + 1, y + h + 1, borderColor)
    fill(x, y, x + w, y + h, bgColor)
    drawCenteredTextWithShadow(tr, label, x + w / 2, y + (h - FONT_HEIGHT) / 2, textColor)
  }

  private fun drawToolButtons() {
    drawCenteredTextWithShadow(tr, "${TranslationString.TOOL}.title".i18n(), layout.toolsX + TOOL_BTN_W / 2, layout.toolsY - LABEL_OFFSET_Y, PaintColors.LABEL)
    for ((i, tool) in PaintTool.entries.withIndex()) {
      val by = layout.toolsY + i * (TOOL_BTN_H + TOOL_BTN_GAP)
      val selected = tool == currentTool
      drawLabeledButton(
        layout.toolsX, by, TOOL_BTN_W, TOOL_BTN_H,
        tool.labelKey.i18n(),
        borderColor = PaintColors.borderColor(selected),
        bgColor = PaintColors.bgColor(selected),
        textColor = PaintColors.TEXT,
      )
    }
  }

  private fun drawBrushSizeButtons() {
    val disabled = currentTool.ignoresBrushSize
    drawCenteredTextWithShadow(
      tr, "${TranslationString.TOOL}.brush".i18n(),
      layout.brushX + BRUSH_BTN_SIZE / 2, layout.brushY - LABEL_OFFSET_Y,
      if (disabled) PaintColors.LABEL_DISABLED else PaintColors.LABEL,
    )
    for (idx in 0 until MAX_BRUSH_SIZE) {
      val size = idx + 1
      val by = layout.brushY + idx * (BRUSH_BTN_SIZE + BRUSH_BTN_GAP)
      val selected = size == brushSize && !disabled
      drawLabeledButton(
        layout.brushX, by, BRUSH_BTN_SIZE, BRUSH_BTN_SIZE,
        "$size",
        borderColor = PaintColors.borderColor(selected, disabled),
        bgColor = PaintColors.bgColor(selected, disabled),
        textColor = if (disabled) PaintColors.LABEL_DISABLED else PaintColors.TEXT,
      )
    }
  }

  private fun drawPalette() {
    fun swatchX(i: Int) = layout.paletteX + i * (PALETTE_SWATCH_SIZE + PALETTE_GAP)

    for (i in 0 until PALETTE_SIZE) {
      if (i == selectedColor) continue
      val sx = swatchX(i)
      fill(sx - 1, layout.paletteY - 1, sx + PALETTE_SWATCH_SIZE + 1, layout.paletteY + PALETTE_SWATCH_SIZE + 1, PaintColors.PALETTE_BORDER)
      fill(sx, layout.paletteY, sx + PALETTE_SWATCH_SIZE, layout.paletteY + PALETTE_SWATCH_SIZE, getGLColor(i))
    }

    val sx = swatchX(selectedColor)
    fill(sx - 2, layout.paletteY - 2, sx + PALETTE_SWATCH_SIZE + 2, layout.paletteY + PALETTE_SWATCH_SIZE + 2, PaintColors.BTN_BORDER_SELECTED)
    fill(sx - 1, layout.paletteY - 1, sx + PALETTE_SWATCH_SIZE + 1, layout.paletteY + PALETTE_SWATCH_SIZE + 1, PaintColors.PALETTE_BORDER)
    fill(sx, layout.paletteY, sx + PALETTE_SWATCH_SIZE, layout.paletteY + PALETTE_SWATCH_SIZE, getGLColor(selectedColor))

    val colorName = "${TranslationString.COLOR}.${DyeItem.names[selectedColor]}".i18n()
    drawCenteredTextWithShadow(tr, colorName, width / 2, layout.paletteY - LABEL_OFFSET_Y, PaintColors.LABEL)
  }

  override fun shouldPause(): Boolean = false
}
