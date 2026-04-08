package pw.tmpim.goodfarming.item

import net.minecraft.item.ItemStack
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases
import net.modificationstation.stationapi.api.util.Identifier
import pw.tmpim.goodfarming.GoodFarming.namespace
import pw.tmpim.goodutils.block.BlockOrTag
import pw.tmpim.goodutils.item.ItemOrTag
import pw.tmpim.goodutils.item.matches
import pw.tmpim.goodutils.item.toFirstItemStack
import pw.tmpim.goodutils.misc.clearableLazy

data class SeedType(
  val id: Identifier,
  val item: ItemOrTag,
  val textureId: Identifier? = null,
  val plantOnBlocks: List<BlockOrTag>? = null,
) {
  internal val texture by lazy {
    textureId?.let { itemAtlas.addTexture(it) } ?: baseTexture
  }

  internal fun matches(other: ItemStack) =
    item.matches(other)

  val lazyFirstItem = clearableLazy { item.toFirstItemStack() }
  val cachedFirstItem by lazyFirstItem

  companion object {
    private val itemAtlas by lazy { Atlases.getGuiItems() }

    private val baseTextureId = namespace.id("item/seed_bag")
    internal val baseTexture by lazy { itemAtlas.addTexture(baseTextureId) }
  }
}
