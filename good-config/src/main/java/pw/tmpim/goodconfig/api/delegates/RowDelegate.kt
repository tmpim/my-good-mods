package pw.tmpim.goodconfig.api.delegates

import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Codec
import com.mojang.serialization.DynamicOps
import pw.tmpim.goodconfig.api.ContainerDelegate
import pw.tmpim.goodconfig.api.RowSpec
import pw.tmpim.goodconfig.api.SchemaDelegate
import pw.tmpim.goodconfig.api.SyncDirection

class RowDelegate<R : RowSpec>(
  name: String,
  description: String? = null,
  key: String? = null,
  factory: () -> R,
) : SchemaDelegate<R>(name, description, factory(), SyncDirection.NONE, false, key), ContainerDelegate<R> {
  override val spec: R
    get() = value

  override fun onKeyRenamed(oldKey: String, newKey: String) {
    spec.containerName = newKey
  }

  override val codec = object : Codec<R> {
    override fun <O> encode(input: R, ops: DynamicOps<O>, prefix: O) =
      spec.codec.encode(Unit, ops, prefix)

    override fun <O> decode(ops: DynamicOps<O>, input: O) =
      spec.codec.decode(ops, input).map { Pair.of(spec, it.second) }
  }
}
