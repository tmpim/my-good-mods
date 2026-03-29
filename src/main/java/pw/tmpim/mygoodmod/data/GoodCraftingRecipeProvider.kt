package pw.tmpim.mygoodmod.data

import emmathemartian.datagen.DataGenContext
import emmathemartian.datagen.provider.CraftingRecipeProvider
import emmathemartian.datagen.util.DataIngredient
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import pw.tmpim.mygoodmod.MyGoodMod

class GoodCraftingRecipeProvider(ctx: DataGenContext) : CraftingRecipeProvider(ctx) {
  override fun run(ctx: DataGenContext) {
    shaped()
      .pattern("RRR")
      .pattern("RRR")
      .pattern("RRR")
      .define('R', DataIngredient.of(Item.REDSTONE))
      .result(ItemStack(MyGoodMod.redstoneBlock))
      .save("redstone_block", this, ctx)
  }
}
