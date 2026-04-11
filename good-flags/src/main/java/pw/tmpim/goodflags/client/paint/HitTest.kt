package pw.tmpim.goodflags.client.paint

/**
 * Utility for hit-testing evenly-spaced button strips (palettes, tool bars, etc.).
 */
object HitTest {
  /**
   * Returns the 0-based index of the hit item in a button strip, or -1 if none.
   *
   * The strip is a row of [count] items laid out along the primary axis starting at
   * ([primaryOrigin], [crossOrigin]). Each item has size [itemPrimary] x [itemCross]
   * with [gap] pixels between items.
   *
   * @param mousePrimary  mouse coordinate along the strip's primary axis
   * @param mouseCross    mouse coordinate along the strip's cross axis
   * @param primaryOrigin starting position along the primary axis
   * @param crossOrigin   starting position along the cross axis
   * @param itemPrimary   item extent along the primary axis
   * @param itemCross     item extent along the cross axis
   * @param gap           gap between items along the primary axis
   * @param count         number of items in the strip
   */
  fun stripIndex(
    mousePrimary: Int, mouseCross: Int,
    primaryOrigin: Int, crossOrigin: Int,
    itemPrimary: Int, itemCross: Int,
    gap: Int, count: Int,
  ): Int {
    if (mouseCross !in crossOrigin until crossOrigin + itemCross) return -1
    val offset = mousePrimary - primaryOrigin
    if (offset < 0) return -1
    val slot = offset / (itemPrimary + gap)
    if (slot >= count) return -1
    if (offset - slot * (itemPrimary + gap) >= itemPrimary) return -1
    return slot
  }

  /** Hit-test a vertical strip (items stacked top-to-bottom). */
  fun vertical(
    mouseX: Int, mouseY: Int,
    originX: Int, originY: Int,
    itemWidth: Int, itemHeight: Int,
    gap: Int, count: Int,
  ): Int = stripIndex(mouseY, mouseX, originY, originX, itemHeight, itemWidth, gap, count)

  /** Hit-test a horizontal strip (items laid out left-to-right). */
  fun horizontal(
    mouseX: Int, mouseY: Int,
    originX: Int, originY: Int,
    itemWidth: Int, itemHeight: Int,
    gap: Int, count: Int,
  ): Int = stripIndex(mouseX, mouseY, originX, originY, itemWidth, itemHeight, gap, count)
}
