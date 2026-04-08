package pw.tmpim.goodfarming.api

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.mojang.datafixers.util.Either
import emmathemartian.datagen.IDataBuilder
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.modificationstation.stationapi.api.tag.TagKey
import pw.tmpim.goodfarming.data.JsonCropType
import pw.tmpim.goodutils.block.BlockOrTag
import pw.tmpim.goodutils.block.BlockWithMeta
import pw.tmpim.goodutils.data.toJsonBlockKey
import pw.tmpim.goodutils.data.toJsonItemKey
import pw.tmpim.goodutils.item.ItemOrTag
import pw.tmpim.goodutils.item.ItemWithDamage

class CropTypeBuilder : IDataBuilder {
  private var crops = mutableListOf<BlockOrTag>()
  private var seeds = mutableListOf<ItemOrTag>()

  fun crop(block: Block, meta: Int = -1) =
    apply { crops.add(Either.left(BlockWithMeta(block, meta))) }
  fun crop(tag: TagKey<Block>) =
    apply { crops.add(Either.right(tag)) }

  fun seed(seed: ItemOrTag) =
    apply { this.seeds.add(seed) }
  fun seed(item: Item, damage: Int = -1) =
    apply { this.seed(Either.left(ItemWithDamage(item, damage))) }
  fun seed(stack: ItemStack) =
    apply { this.seed(Either.left(ItemWithDamage(stack))) }
  fun seed(tag: TagKey<Item>) =
    apply { this.seed(Either.right(tag)) }

  override fun build(): JsonObject {
    // validate before building
    check(crops.isNotEmpty()) { "at least one crop must be specified" }

    // if no seeds are specified, that's fine; the crop will still right-click to harvest, but not replant

    return gson.toJsonTree(JsonCropType(
      crops = crops.map { it.toJsonBlockKey() },
      seeds = seeds.map { it.toJsonItemKey() }
    )) as JsonObject
  }

  companion object {
    private val gson = GsonBuilder().setPrettyPrinting().create()
  }
}
