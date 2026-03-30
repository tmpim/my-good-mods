package pw.tmpim.mygoodmod.data

import emmathemartian.datagen.DataGenContext
import emmathemartian.datagen.provider.CraftingRecipeProvider
import emmathemartian.datagen.util.DataIngredient
import net.minecraft.block.Block
import net.minecraft.item.BlockItem
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
    shapeless()
      .ingredient(DataIngredient.of(MyGoodMod.redstoneBlock.asItem()))
      .result(ItemStack(Item.REDSTONE, 9))
      .save("redstone", this, ctx)
    shaped()
      .pattern("SS")
      .pattern("SS")
      .define('S', DataIngredient.of(Block.STONE.asItem()))
      .result(ItemStack(MyGoodMod.stoneBricksBlock, 4))
      .save("stone_bricks", this, ctx)
  }
}
