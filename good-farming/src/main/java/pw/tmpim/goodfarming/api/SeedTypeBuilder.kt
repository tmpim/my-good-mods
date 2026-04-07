package pw.tmpim.goodfarming.api

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.mojang.datafixers.util.Either
import emmathemartian.datagen.IDataBuilder
import emmathemartian.datagen.util.DataIngredient
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.modificationstation.stationapi.api.tag.TagKey
import net.modificationstation.stationapi.api.util.Identifier
import pw.tmpim.goodfarming.data.JsonSeedType
import pw.tmpim.goodutils.data.toJsonBlockKey
import pw.tmpim.goodutils.data.toJsonItemKey

class SeedTypeBuilder : IDataBuilder {
  private var item: DataIngredient? = null
  private var textureId: String? = null
  private var plantOnBlocks = mutableListOf<Either<Pair<Block, Int>, TagKey<Block>>>()

  fun item(item: DataIngredient) = apply { this.item = item }
  fun item(item: Item) = apply { this.item(DataIngredient.of(item)) }
  fun item(stack: ItemStack) = apply { item(DataIngredient.of(stack)) }

  fun textureId(name: String) = apply { textureId = name }
  fun textureId(name: Identifier) = apply { textureId(name.toString()) }

  fun plantOnBlock(block: Block, meta: Int = -1) = apply { plantOnBlocks.add(Either.left(block to meta)) }
  fun plantOnBlock(tag: TagKey<Block>) = apply { plantOnBlocks.add(Either.right(tag)) }

  override fun build(): JsonObject {
    // validate before building
    val item = checkNotNull(item) { "seed item must be specified" }

    return gson.toJsonTree(JsonSeedType(
      item = item.toJsonItemKey(),
      textureId = textureId,
      plantOnBlocks = plantOnBlocks
        .map { it.toJsonBlockKey() }
        .takeUnless { it.isEmpty() }
    )) as JsonObject
  }

  companion object {
    private val gson = GsonBuilder().setPrettyPrinting().create()
  }
}
