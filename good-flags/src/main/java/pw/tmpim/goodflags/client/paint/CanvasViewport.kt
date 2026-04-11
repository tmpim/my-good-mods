package pw.tmpim.goodflags.client.paint

import pw.tmpim.goodflags.block.FlagSpec.FLAG_HEIGHT
import pw.tmpim.goodflags.block.FlagSpec.FLAG_WIDTH

/**
 * Maps between screen coordinates and pixel coordinates for the flag canvas.
 * Encapsulates canvas position, scale, and coordinate conversion.
 */
class CanvasViewport(
  private val pixelScale: Int = 4,
) {
  val canvasWidth = FLAG_WIDTH * pixelScale
  val canvasHeight = FLAG_HEIGHT * pixelScale

  var canvasX = 0
    private set
  var canvasY = 0
    private set

  /** Recalculate canvas origin from screen dimensions and a vertical offset. */
  fun layout(screenWidth: Int, screenHeight: Int, verticalOffset: Int) {
    canvasX = (screenWidth - canvasWidth) / 2
    canvasY = (screenHeight - canvasHeight) / 2 - verticalOffset
  }

  fun toPixelX(screenX: Int) = (screenX - canvasX) / pixelScale
  fun toPixelY(screenY: Int) = (screenY - canvasY) / pixelScale

  fun isInsideCanvas(mouseX: Int, mouseY: Int): Boolean =
    mouseX in canvasX until canvasX + canvasWidth &&
      mouseY in canvasY until canvasY + canvasHeight

  /** Convert pixel coordinates to screen coordinates for the top-left of the pixel. */
  fun toScreenX(px: Int) = canvasX + px * pixelScale
  fun toScreenY(py: Int) = canvasY + py * pixelScale

  /** The screen-space extent of one pixel. */
  val scale get() = pixelScale
}
