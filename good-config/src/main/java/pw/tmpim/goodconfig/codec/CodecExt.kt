package pw.tmpim.goodconfig.codec

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import java.util.function.Function

fun <N> checkRange(
  minInclusive: N,
  maxInclusive: N
): Function<N, DataResult<N>> where N : Number, N : Comparable<N> {
  return Function { value ->
    if (value in minInclusive..maxInclusive) return@Function DataResult.success<N>(value)
    DataResult.error<N> { "Value $value outside of range [$minInclusive:$maxInclusive]" }
  }
}

fun byteRange(minInclusive: Byte, maxInclusive: Byte): Codec<Byte> {
  val checker = checkRange(minInclusive, maxInclusive)
  return Codec.BYTE.flatXmap(checker, checker)
}

fun longRange(minInclusive: Long, maxInclusive: Long): Codec<Long> {
  val checker = checkRange(minInclusive, maxInclusive)
  return Codec.LONG.flatXmap(checker, checker)
}

fun shortRange(minInclusive: Short, maxInclusive: Short): Codec<Short> {
  val checker = checkRange(minInclusive, maxInclusive)
  return Codec.SHORT.flatXmap(checker, checker)
}
