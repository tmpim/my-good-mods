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

// TODO: Remove for DFU v9
fun checkStringRange(
  minSize: Int,
  maxSize: Int
): Function<String, DataResult<String>> {
  return Function { value ->
    if (value.length in minSize..maxSize) return@Function DataResult.success(value)
    DataResult.error { "String \"$value\" length (${value.length}) outside of range [$minSize:$maxSize]" }
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

// TODO: Remove for DFU v9
fun string(minSize: Int, maxSize: Int): Codec<String> {
  val checker = checkStringRange(minSize, maxSize)
  return Codec.STRING.flatXmap(checker, checker)
}
