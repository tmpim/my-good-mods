package pw.tmpim.goodutils.world

import net.minecraft.world.World

fun World.spiral3D(cx: Int, cy: Int, cz: Int, rX: Int, rY: Int) =
  spiral3D(cx, cy, cz, rX, rY, bottomY, topY)

fun spiral3D(
  cx: Int,
  cy: Int,
  cz: Int,
  rX: Int,
  rY: Int,
  minY: Int,
  maxY: Int,
): Sequence<Triple<Int, Int, Int>> = sequence {
  val yOffsets = sequence { // 0, +1, -1, +2, -2, ...
    yield(0)
    for (r in 1..rY) {
      yield(r)
      yield(-r)
    }
  }

  for (dy in yOffsets) {
    if (cy + dy !in minY..<maxY) {
      continue // skip the y layer if it's out of the world
    }

    // shell 0: center column
    yield(Triple(cx, cy + dy, cz))

    // shell r: walk the square perimeter
    for (r in 1..rX) {
      var x = -r
      var z = -r

      while (x < r)  { yield(Triple(cx + x, cy + dy, cz + z)); x++ }
      while (z < r)  { yield(Triple(cx + x, cy + dy, cz + z)); z++ }
      while (x > -r) { yield(Triple(cx + x, cy + dy, cz + z)); x-- }
      while (z > -r) { yield(Triple(cx + x, cy + dy, cz + z)); z-- }
    }
  }
}
