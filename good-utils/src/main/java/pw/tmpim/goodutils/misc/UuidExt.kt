package pw.tmpim.goodutils.misc

import java.util.*

fun tryParseUuid(input: String?) = try {
  input?.let { UUID.fromString(it) }
} catch (_: Exception) {
  null
}

fun IntArray.toUuid(): UUID {
  require(size == 4) { "array must have exactly 4 elements" }
  val m = (this[0].toLong() shl 32) or (this[1].toLong() and 0xFFFFFFFFL)
  val l = (this[2].toLong() shl 32) or (this[3].toLong() and 0xFFFFFFFFL)
  return UUID(m, l)
}

fun UUID.toIntArray(): IntArray {
  val m = mostSignificantBits
  val l = leastSignificantBits
  return intArrayOf((m shr 32).toInt(), m.toInt(), (l shr 32).toInt(), (l shr 32).toInt())
}
