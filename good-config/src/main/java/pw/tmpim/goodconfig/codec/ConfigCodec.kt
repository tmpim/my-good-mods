package pw.tmpim.goodconfig.codec

import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import pw.tmpim.goodconfig.api.SchemaDelegate

@Suppress("UNCHECKED_CAST")
internal fun buildConfigCodec(
  entries: () -> Map<String, SchemaDelegate<*>>
): Codec<Unit> = object : Codec<Unit> {
  override fun <O> encode(
    input: Unit?,
    ops: DynamicOps<O>,
    prefix: O
  ): DataResult<O> {
    val builder = ops.mapBuilder()

    entries().values.forEach { d ->
      builder.add(d.serialisedKey, d.encodeValue(ops))
    }

    return builder.build(prefix)
  }

  override fun <O> decode(
    ops: DynamicOps<O>,
    input: O
  ): DataResult<Pair<Unit, O>> {
    val mapResult = ops.getMap(input)

    mapResult.result().ifPresent { mapView ->
      entries().values.forEach { d ->
        mapView.get(d.serialisedKey)?.let { rawField ->
          d.decodeAndStore(ops, rawField)
        }
      }
    }

    return mapResult.map { Pair.of(Unit, ops.empty()) }
  }
}
