package pw.tmpim.goodfarming.data

import emmathemartian.datagen.DataGenContext
import emmathemartian.datagen.provider.CraftingRecipeProvider
import emmathemartian.datagen.util.DataIngredient
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import pw.tmpim.goodfarming.GoodFarming

class GoodFarmingCraftingRecipeProvider(ctx: DataGenContext) : CraftingRecipeProvider(ctx) {
  override fun run(ctx: DataGenContext) {
    // Seed Bag
    shaped()
      .pattern("S")
      .pattern("L")
      .define('S', DataIngredient.of(Item.STRING))
      .define('L', DataIngredient.of(Item.LEATHER))
      .result(ItemStack(GoodFarming.seedBag, 1))
      .save("seed_bag", this, ctx)
  }
}
