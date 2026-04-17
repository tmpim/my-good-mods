package pw.tmpim.goodconfig.api.delegates

import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Codec
import com.mojang.serialization.DynamicOps
import pw.tmpim.goodconfig.api.ConfigSpec
import pw.tmpim.goodconfig.api.ContainerDelegate
import pw.tmpim.goodconfig.api.SchemaDelegate
import pw.tmpim.goodconfig.api.SyncDirection

class CategoryDelegate<C : ConfigSpec>(
  name: String,
  override val spec: C,
  key: String? = null,
) : SchemaDelegate<C>(name, null, spec, SyncDirection.NONE, false, key), ContainerDelegate<C> {
  override fun onKeyRenamed(oldKey: String, newKey: String) {
    spec.containerName = newKey
  }

  override val codec = object : Codec<C> {
    override fun <O> encode(input: C, ops: DynamicOps<O>, prefix: O) =
      spec.codec.encode(Unit, ops, prefix)

    override fun <O> decode(ops: DynamicOps<O>, input: O) =
      spec.codec.decode(ops, input).map { Pair.of(spec, it.second) }
  }
}
