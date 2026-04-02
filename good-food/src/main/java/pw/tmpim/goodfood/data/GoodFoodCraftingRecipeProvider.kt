package pw.tmpim.goodfood.data

import emmathemartian.datagen.DataGenContext
import emmathemartian.datagen.provider.CraftingRecipeProvider
import emmathemartian.datagen.util.DataIngredient
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import pw.tmpim.goodfood.GoodFood

class GoodFoodCraftingRecipeProvider(ctx: DataGenContext) : CraftingRecipeProvider(ctx) {
  override fun run(ctx: DataGenContext) {
    // Bagel
    shaped()
      .pattern("WWW")
      .pattern("W W")
      .pattern("WWW")
      .define('W', DataIngredient.of(Item.WHEAT))
      .result(ItemStack(GoodFood.bagelItem, 2))
      .save("bagel", this, ctx)
  }
}
