package pw.tmpim.goodutils.data

import com.mojang.datafixers.util.Either
import net.minecraft.block.Block
import net.modificationstation.stationapi.api.registry.BlockRegistry
import net.modificationstation.stationapi.api.tag.TagKey
import net.modificationstation.stationapi.api.util.Identifier
import kotlin.jvm.optionals.getOrNull

// complement to stapi's JsonItemKey
data class JsonBlockKey(
  val block: String?,
  val meta: Int,
  val tag: String?
) {
  fun getRegisteredBlock(): Pair<Block, Int> =
    checkNotNull(block) { "block is null" }.let {
      checkNotNull(BlockRegistry.INSTANCE.get(Identifier.of(it))) { "block not found" } to meta
    }

  fun getRegisteredTag(): TagKey<Block> =
    TagKey.of(BlockRegistry.KEY, Identifier.of(checkNotNull(tag) { "tag is null" }))

  fun get(): Either<Pair<Block, Int>, TagKey<Block>> = when {
    block != null && tag == null ->
      Either.left(getRegisteredBlock())
    block == null && tag != null ->
      Either.right(getRegisteredTag())
    else -> error("Neither item nor tag, or both are specified in the JsonItemKey!")
  }
}

fun Either<Pair<Block, Int>, TagKey<Block>>.toJsonBlockKey(): JsonBlockKey {
  val srcBlock = left()
    .map { (id, meta) ->
      checkNotNull(BlockRegistry.INSTANCE.getId(id)) { "block $id not found in registry" } to meta
    }
    .getOrNull()
  val srcTag = right().getOrNull()
  check(srcBlock != null || srcTag != null) { "either block or tag must be specified" }

  return JsonBlockKey(
    block = srcBlock?.first?.toString(),
    meta  = srcBlock?.second ?: -1,
    tag   = srcTag?.id?.toString(),
  )
}
