package pw.tmpim.goodflags.data

import emmathemartian.datagen.DataGenContext
import emmathemartian.datagen.provider.CraftingRecipeProvider
import emmathemartian.datagen.util.DataIngredient
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import pw.tmpim.goodflags.GoodFlags

class GoodFlagsCraftingRecipeProvider(ctx: DataGenContext) : CraftingRecipeProvider(ctx) {
  override fun run(ctx: DataGenContext) {
    shaped()
      .pattern("SWW")
      .pattern("SWW")
      .pattern("S  ")
      .define('S', DataIngredient.of(Item.STICK))
      .define('W', DataIngredient.of(Block.WOOL.asItem(), 1, -1))
      .result(ItemStack(GoodFlags.flagBlock))
      .save("flag", this, ctx)
  }
}
