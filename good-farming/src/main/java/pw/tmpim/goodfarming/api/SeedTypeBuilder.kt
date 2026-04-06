package pw.tmpim.goodfarming.api

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import emmathemartian.datagen.IDataBuilder
import emmathemartian.datagen.util.DataIngredient
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import pw.tmpim.goodfarming.data.JsonSeedType
import pw.tmpim.goodutils.data.toJsonItemKey

class SeedTypeBuilder : IDataBuilder {
  private var item: DataIngredient? = null
  private var textureName: String? = null
  private var plantOnBlocks = mutableListOf<DataIngredient>()

  fun item(item: DataIngredient) = apply { this.item = item }
  fun item(item: Item) = apply { this.item(DataIngredient.of(item)) }
  fun item(stack: ItemStack) = apply { this.item(DataIngredient.of(stack)) }

  fun textureName(name: String) = apply { this.textureName = name }

  fun plantOnBlock(block: DataIngredient) = apply { this.plantOnBlocks.add(block) }
  fun plantOnBlock(block: Item) = apply { this.plantOnBlock(DataIngredient.of(block)) }
  fun plantOnBlock(block: Block) = apply { this.plantOnBlock(DataIngredient.of(block.asItem())) }
  fun plantOnBlock(stack: ItemStack) = apply { this.plantOnBlock(DataIngredient.of(stack)) }

  override fun build(): JsonObject {
    // validate before building
    val item = checkNotNull(item) { "seed item must be specified" }

    return gson.toJsonTree(JsonSeedType(
      item = item.toJsonItemKey(),
      textureName = textureName,
      plantOnBlocks = plantOnBlocks
        .map { it.toJsonItemKey() }
        .takeUnless { it.isEmpty() }
    )) as JsonObject
  }

  companion object {
    private val gson = GsonBuilder().setPrettyPrinting().create()
  }
}
