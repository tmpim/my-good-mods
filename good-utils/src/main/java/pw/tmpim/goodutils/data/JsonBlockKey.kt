package pw.tmpim.goodutils.data

import com.mojang.datafixers.util.Either
import net.minecraft.block.Block
import net.modificationstation.stationapi.api.registry.BlockRegistry
import net.modificationstation.stationapi.api.tag.TagKey
import net.modificationstation.stationapi.api.util.Identifier
import pw.tmpim.goodutils.block.BlockOrTag
import pw.tmpim.goodutils.block.BlockWithMeta
import kotlin.jvm.optionals.getOrNull

// based on stapi's JsonItemKey
data class JsonBlockKey(
  val id: String?,
  val meta: Int,
  val tag: String?
) {
  fun getRegisteredBlock(): BlockWithMeta {
    val blockId = Identifier.of(checkNotNull(id) { "block is null" })

    return BlockWithMeta(
      block = checkNotNull(BlockRegistry.INSTANCE.get(blockId)) { "block not found" },
      meta  = meta
    )
  }

  fun getRegisteredTag(): TagKey<Block> =
    TagKey.of(BlockRegistry.KEY, Identifier.of(checkNotNull(tag) { "tag is null" }))

  fun get(): BlockOrTag = when {
    id != null && tag == null ->
      Either.left(getRegisteredBlock())

    id == null && tag != null ->
      Either.right(getRegisteredTag())

    else -> error("Neither id nor tag, or both are specified in the JsonItemKey!")
  }
}

fun BlockOrTag.toJsonBlockKey(): JsonBlockKey {
  val srcBlock = left()
    .map { (id, meta) ->
      checkNotNull(BlockRegistry.INSTANCE.getId(id)) { "block $id not found in registry" } to meta
    }
    .getOrNull()
  val srcTag = right().getOrNull()

  check(srcBlock != null || srcTag != null) { "either id or tag must be specified" }

  return JsonBlockKey(
    id   = srcBlock?.first?.toString(),
    meta = srcBlock?.second ?: -1,
    tag  = srcTag?.id?.toString(),
  )
}
