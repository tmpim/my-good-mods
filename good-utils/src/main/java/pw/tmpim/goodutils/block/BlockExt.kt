package pw.tmpim.goodutils.block

import com.mojang.datafixers.util.Either
import net.minecraft.block.Block
import net.modificationstation.stationapi.api.block.BlockState
import net.modificationstation.stationapi.api.tag.TagKey

data class BlockWithMeta(val block: Block, val meta: Int = -1)

typealias BlockOrTag = Either<BlockWithMeta, TagKey<Block>>

fun BlockOrTag.matches(otherState: BlockState, otherMeta: Int): Boolean =
  map(
    { (block, meta) -> block == otherState.block && (meta == -1 || meta == otherMeta) },
    { tag -> otherState.isIn(tag) }
  )
