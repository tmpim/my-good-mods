package pw.tmpim.goodflags.client.paint

/**
 * Mutable state for an in-progress mouse stroke (pencil drag, shape drag, etc.).
 */
class StrokeState {
  var active = false
    private set
  var lastPx = -1
    private set
  var lastPy = -1
    private set
  var shapeStartX = -1
    private set
  var shapeStartY = -1
    private set
  var snapshot: ByteArray? = null
    private set

  val hasShapeStart get() = shapeStartX >= 0 && shapeStartY >= 0

  /** Begin a freehand stroke (pencil/eraser). */
  fun beginFreehand(px: Int, py: Int, pixelSnapshot: ByteArray) {
    active = true
    lastPx = px
    lastPy = py
    snapshot = pixelSnapshot
  }

  /** Begin a shape stroke (line/rect/circle). */
  fun beginShape(px: Int, py: Int, pixelSnapshot: ByteArray) {
    active = true
    shapeStartX = px
    shapeStartY = py
    snapshot = pixelSnapshot
  }

  /** Update the last-painted position during freehand drawing. */
  fun updateLastPosition(px: Int, py: Int) {
    lastPx = px
    lastPy = py
  }

  /** Reset all state after a stroke is finished. */
  fun reset() {
    active = false
    lastPx = -1
    lastPy = -1
    shapeStartX = -1
    shapeStartY = -1
    snapshot = null
  }
}
