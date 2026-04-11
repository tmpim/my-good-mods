package pw.tmpim.goodflags.client.paint

import pw.tmpim.goodflags.block.FlagSpec.FLAG_HEIGHT
import pw.tmpim.goodflags.block.FlagSpec.FLAG_WIDTH

/**
 * Logical pixel canvas backed by a flat byte array.
 *
 * Encapsulates all pixel-level operations (set, brushed paint, shapes, flood fill)
 * and integrates with [UndoRedoHistory] for undo/redo support.
 */
class PixelCanvas(sourcePixels: ByteArray) {
  companion object {
    private const val COLOR_MASK = 0xF
    const val ERASER_COLOR = 15
    private const val MAX_UNDO = 32

    fun packCoords(x: Int, y: Int): Long =
      (x.toLong() shl 32) or (y.toLong() and 0xFFFFFFFFL)

    fun unpackCoords(packed: Long): Pair<Int, Int> =
      (packed shr 32).toInt() to packed.toInt()
  }

  val pixels: ByteArray = sourcePixels.copyOf()
  private val history = UndoRedoHistory(MAX_UNDO)

  // ── Single pixel ──────────────────────────────────────────────────────────

  /** Read the color index of a pixel, or -1 if out of bounds. */
  fun getPixelColor(x: Int, y: Int): Int =
    if (x in 0 until FLAG_WIDTH && y in 0 until FLAG_HEIGHT)
      pixels[y * FLAG_WIDTH + x].toInt() and COLOR_MASK
    else -1

  /** Set a single pixel with bounds checking. */
  fun setPixel(x: Int, y: Int, color: Int) {
    if (x in 0 until FLAG_WIDTH && y in 0 until FLAG_HEIGHT) {
      pixels[y * FLAG_WIDTH + x] = (color and COLOR_MASK).toByte()
    }
  }

  // ── Brush helpers ─────────────────────────────────────────────────────────

  /** Iterate over every pixel in the brush kernel centered on (cx, cy). */
  inline fun forEachBrushPixel(cx: Int, cy: Int, brushSize: Int, action: (Int, Int) -> Unit) {
    val half = brushSize / 2
    for (dy in -half until brushSize - half) {
      for (dx in -half until brushSize - half) {
        action(cx + dx, cy + dy)
      }
    }
  }

  /** Paint a single brushed stamp at (px, py). */
  fun paintBrushed(px: Int, py: Int, color: Int, brushSize: Int) {
    forEachBrushPixel(px, py, brushSize) { x, y -> setPixel(x, y, color) }
  }

  /** Paint a brushed Bresenham line from (x0, y0) to (x1, y1). */
  fun paintLineBrushed(x0: Int, y0: Int, x1: Int, y1: Int, color: Int, brushSize: Int) {
    DrawingAlgorithms.bresenham(x0, y0, x1, y1) { cx, cy ->
      forEachBrushPixel(cx, cy, brushSize) { x, y -> setPixel(x, y, color) }
    }
  }

  // ── Shape pixel generation ────────────────────────────────────────────────

  /**
   * Generate all pixel coordinates affected by a shape tool drag from (x0,y0) to (x1,y1).
   *
   * This is the single source of truth for shape geometry — both committing to the
   * canvas and rendering a preview use this same method. Only in-bounds pixels are yielded.
   *
   * @param tool      the shape tool (LINE, RECT, or CIRCLE)
   * @param x0        drag start x (pixel coords)
   * @param y0        drag start y (pixel coords)
   * @param x1        drag end x (pixel coords)
   * @param y1        drag end y (pixel coords)
   * @param brushSize brush size (only affects LINE; RECT and CIRCLE ignore it)
   * @param action    callback invoked for each affected pixel coordinate
   */
  inline fun forEachShapePixel(
    tool: PaintTool,
    x0: Int, y0: Int, x1: Int, y1: Int,
    brushSize: Int,
    action: (Int, Int) -> Unit,
  ) {
    when (tool) {
      PaintTool.LINE -> {
        DrawingAlgorithms.bresenham(x0, y0, x1, y1) { cx, cy ->
          forEachBrushPixel(cx, cy, brushSize) { x, y ->
            if (x in 0 until FLAG_WIDTH && y in 0 until FLAG_HEIGHT) action(x, y)
          }
        }
      }
      PaintTool.RECT -> {
        val minX = minOf(x0, x1); val maxX = maxOf(x0, x1)
        val minY = minOf(y0, y1); val maxY = maxOf(y0, y1)
        for (py in minY..maxY) for (px in minX..maxX) {
          if (px in 0 until FLAG_WIDTH && py in 0 until FLAG_HEIGHT) action(px, py)
        }
      }
      PaintTool.CIRCLE -> {
        DrawingAlgorithms.filledEllipse(minOf(x0, x1), minOf(y0, y1), maxOf(x0, x1), maxOf(y0, y1)) { px, py ->
          if (px in 0 until FLAG_WIDTH && py in 0 until FLAG_HEIGHT) action(px, py)
        }
      }
      else -> {} // non-shape tools: no-op
    }
  }

  /** Apply a shape tool to the pixel buffer (commit). */
  fun applyShape(tool: PaintTool, x0: Int, y0: Int, x1: Int, y1: Int, color: Int, brushSize: Int) {
    forEachShapePixel(tool, x0, y0, x1, y1, brushSize) { x, y -> setPixel(x, y, color) }
  }

  /** Collect the pixel coordinates a shape tool would affect (for preview). */
  fun collectShapePixels(tool: PaintTool, x0: Int, y0: Int, x1: Int, y1: Int, brushSize: Int): Set<Long> {
    val result = mutableSetOf<Long>()
    forEachShapePixel(tool, x0, y0, x1, y1, brushSize) { x, y -> result.add(packCoords(x, y)) }
    return result
  }

  /** 4-connected flood fill starting at (startX, startY). */
  fun floodFill(startX: Int, startY: Int, fillColor: Int) {
    DrawingAlgorithms.floodFill(pixels, FLAG_WIDTH, FLAG_HEIGHT, startX, startY, fillColor, COLOR_MASK)
  }

  // ── Undo / redo ───────────────────────────────────────────────────────────

  fun pushUndo(snapshot: ByteArray = pixels.copyOf()) = history.pushUndo(snapshot)

  fun undo() {
    history.undo(pixels)?.let { prev -> System.arraycopy(prev, 0, pixels, 0, pixels.size) }
  }

  fun redo() {
    history.redo(pixels)?.let { next -> System.arraycopy(next, 0, pixels, 0, pixels.size) }
  }

  /** Resolve the effective drawing color, accounting for eraser mode. */
  fun resolveColor(selectedColor: Int, isEraser: Boolean): Int =
    if (isEraser) ERASER_COLOR else selectedColor
}
