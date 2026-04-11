package pw.tmpim.goodflags.client.paint

import kotlin.math.abs

/**
 * Pure computational drawing algorithms with no UI dependencies.
 * Each algorithm accepts a lambda that receives pixel coordinates.
 */
object DrawingAlgorithms {

  // Ellipse pixel-art heuristic thresholds.
  // At these specific diameters the rasterized ellipse needs a ±1px
  // adjustment to look visually round.
  @PublishedApi internal val ELLIPSE_ADJUST_DIAMETERS = intArrayOf(8, 12, 22)

  /** Minimum diameter for ellipse heuristic adjustments to apply. */
  @PublishedApi internal const val ELLIPSE_MIN_ADJUST_DIAMETER = 5

  /** Bresenham line rasterization from (x0, y0) to (x1, y1). */
  inline fun bresenham(x0: Int, y0: Int, x1: Int, y1: Int, plot: (Int, Int) -> Unit) {
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
      if (e2 < dx) { err += dx; cy += sy }
    }
  }

  /** 4-connected flood fill on a flat byte array of [width] x [height]. */
  fun floodFill(
    pixels: ByteArray, width: Int, height: Int,
    startX: Int, startY: Int, fillColor: Int, colorMask: Int,
  ) {
    if (startX !in 0 until width || startY !in 0 until height) return
    val target = pixels[startY * width + startX].toInt() and colorMask
    val fill = fillColor and colorMask
    if (target == fill) return

    val queue = ArrayDeque<Int>()
    queue.add(startY * width + startX)
    while (queue.isNotEmpty()) {
      val idx = queue.removeFirst()
      if (pixels[idx].toInt() and colorMask != target) continue
      pixels[idx] = fill.toByte()
      val x = idx % width
      val y = idx / width
      if (x + 1 < width) queue.add(idx + 1)
      if (x - 1 >= 0) queue.add(idx - 1)
      if (y + 1 < height) queue.add(idx + width)
      if (y - 1 >= 0) queue.add(idx - width)
    }
  }

  /**
   * Filled ellipse ported from Aseprite's algo_ellipsefill / adjust_ellipse_args.
   * Calls [plot] for every pixel inside the ellipse whose bounding box is (x0i, y0i) → (x1i, y1i).
   *
   * The "adjust_ellipse_args" section applies Aseprite's pixel-art heuristics to
   * avoid visual artifacts at specific diameters:
   * - Diameters of 8, 12, and 22 need a one-pixel adjustment to appear round.
   * - Diameters ≤ 5 skip the adjustment entirely (too small to benefit).
   * - Even diameters > 5 get a counter-adjustment to stay symmetric.
   */
  inline fun filledEllipse(x0i: Int, y0i: Int, x1i: Int, y1i: Int, plot: (Int, Int) -> Unit) {
    var x0 = x0i; var y0 = y0i; var x1 = x1i; var y1 = y1i
    var hPixels = 0; var vPixels = 0

    // --- adjust_ellipse_args ---
    hPixels = maxOf(hPixels, 0)
    vPixels = maxOf(vPixels, 0)
    if (x0 > x1) { val t = x0; x0 = x1; x1 = t }
    if (y0 > y1) { val t = y0; y0 = y1; y1 = t }
    val w = x1 - x0 + 1
    val h = y1 - y0 + 1
    val hDiameter = w - hPixels
    val vDiameter = h - vPixels
    if (w in ELLIPSE_ADJUST_DIAMETERS) hPixels++
    if (h in ELLIPSE_ADJUST_DIAMETERS) vPixels++
    hPixels = if (hDiameter > ELLIPSE_MIN_ADJUST_DIAMETER) hPixels else 0
    vPixels = if (vDiameter > ELLIPSE_MIN_ADJUST_DIAMETER) vPixels else 0
    if (hDiameter % 2 == 0 && hDiameter > ELLIPSE_MIN_ADJUST_DIAMETER) hPixels--
    if (vDiameter % 2 == 0 && vDiameter > ELLIPSE_MIN_ADJUST_DIAMETER) vPixels--
    x1 -= hPixels
    y1 -= vPixels
    // --- end adjust_ellipse_args ---

    val a = abs(x1 - x0).toLong()
    val b = abs(y1 - y0).toLong()
    val b1 = b and 1L
    var dx = 4.0 * (1.0 - a) * b * b
    var dy = 4.0 * (b1 + 1) * a * a
    var err = dx + dy + b1 * a * a
    var e2: Double

    y0 += ((b + 1) / 2).toInt()
    y1 = y0 - b1.toInt()
    val aMul = 8 * a * a
    val b1Mul = 8 * b * b

    val initialY0 = y0
    val initialY1 = y1
    val initialX0 = x0
    val initialX1 = x1 + hPixels

    do {
      for (px in x0..x1 + hPixels) {
        plot(px, y0 + vPixels)
        plot(px, y1)
      }
      e2 = 2 * err
      if (e2 <= dy) { y0++; y1--; dy += aMul; err += dy }
      if (e2 >= dx || 2 * err > dy) { x0++; x1--; dx += b1Mul; err += dx }
    } while (x0 <= x1)

    // Too-early stop for flat ellipses (a == 1): finish tips
    while (y0 + vPixels - y1 + 1 <= h) {
      plot(x0 - 1, y0 + vPixels)
      plot(x1 + 1 + hPixels, y0++ + vPixels)
      plot(x0 - 1, y1)
      plot(x1 + 1 + hPixels, y1--)
    }

    // Fill vertical middle band when vPixels > 0
    if (vPixels > 0) {
      for (i in initialY1 + 1 until initialY0 + vPixels) {
        for (px in initialX0..initialX1) plot(px, i)
      }
    }
  }
}
