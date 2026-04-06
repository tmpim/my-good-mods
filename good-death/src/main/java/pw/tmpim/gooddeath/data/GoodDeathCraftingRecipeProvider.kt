package pw.tmpim.gooddeath.data

import emmathemartian.datagen.DataGenContext
import emmathemartian.datagen.provider.CraftingRecipeProvider
import emmathemartian.datagen.util.DataIngredient
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import pw.tmpim.gooddeath.GoodDeath

class GoodDeathCraftingRecipeProvider(ctx: DataGenContext) : CraftingRecipeProvider(ctx) {
  override fun run(ctx: DataGenContext) {
    shaped()
      .pattern("CCC")
      .pattern("CBC")
      .pattern("CCC")
      .define('C', DataIngredient.of(Block.COBBLESTONE.asItem()))
      .define('B', DataIngredient.of(Item.BONE))
      .result(ItemStack(GoodDeath.tombstoneBlock))
      .save("tombstone", this, ctx)
  }
}
