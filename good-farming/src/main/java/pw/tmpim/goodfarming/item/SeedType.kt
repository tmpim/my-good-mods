package pw.tmpim.goodfarming.item

import com.mojang.datafixers.util.Either
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.modificationstation.stationapi.api.block.BlockState
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases
import net.modificationstation.stationapi.api.registry.ItemRegistry
import net.modificationstation.stationapi.api.tag.TagKey
import net.modificationstation.stationapi.api.util.Identifier
import pw.tmpim.goodfarming.GoodFarming.namespace
import kotlin.jvm.optionals.getOrNull

data class SeedType(
  val id: Identifier,
  val item: Either<ItemStack, TagKey<Item>>,
  val textureId: Identifier? = null,
  val plantOnBlocks: List<Either<Pair<Block, Int>, TagKey<Block>>>? = null,
) {
  internal val texture by lazy {
    textureId?.let { itemAtlas.addTexture(it) } ?: baseTexture
  }

  internal fun matches(other: ItemStack) =
    item.matches(other)

  internal val firstItem: ItemStack? =
    item.map(
      { item -> item },
      { tag -> ItemRegistry.INSTANCE.getEntryList(tag).getOrNull()?.firstOrNull()?.value()?.let { ItemStack(it) } },
    )

  companion object {
    private val itemAtlas by lazy {
      Atlases.getGuiItems()
    }

    private val baseTextureId = namespace.id("item/seed_bag")
    internal val baseTexture by lazy {
      itemAtlas.addTexture(baseTextureId)
    }

    internal fun Either<ItemStack, TagKey<Item>>.matches(other: ItemStack) =
      map(
        { item -> item.isItemEqual(other) /* checks damage and NBT */ },
        { tag -> other.isIn(tag) }
      )

    internal fun Either<Pair<Block, Int>, TagKey<Block>>.matches(
      otherState: BlockState,
      otherMeta: Int,
    ) =
      map(
        { block -> block.first == otherState.block && block.second == otherMeta },
        { tag -> otherState.isIn(tag) }
      )
  }
}
