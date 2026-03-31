package pw.tmpim.mygoodmod.data

import emmathemartian.datagen.DataGenContext
import emmathemartian.datagen.provider.CraftingRecipeProvider
import emmathemartian.datagen.util.DataIngredient
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import pw.tmpim.mygoodmod.MyGoodMod

class GoodCraftingRecipeProvider(ctx: DataGenContext) : CraftingRecipeProvider(ctx) {
  override fun run(ctx: DataGenContext) {
    // Redstone Block
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

    // Hay Bale
    shaped()
      .pattern("WWW")
      .pattern("WWW")
      .pattern("WWW")
      .define('W', DataIngredient.of(Item.WHEAT))
      .result(ItemStack(MyGoodMod.hayBlock))
      .save("hay_bale", this, ctx)
    shapeless()
      .ingredient(DataIngredient.of(MyGoodMod.hayBlock.asItem()))
      .result(ItemStack(Item.WHEAT, 9))
      .save("wheat", this, ctx)

    // Coal Block
    shaped()
      .pattern("CCC")
      .pattern("CCC")
      .pattern("CCC")
      .define('C', DataIngredient.of(Item.COAL))
      .result(ItemStack(MyGoodMod.coalBlock))
      .save("coal_block", this, ctx)
    shapeless()
      .ingredient(DataIngredient.of(MyGoodMod.coalBlock.asItem()))
      .result(ItemStack(Item.COAL, 9))
      .save("coal", this, ctx)

    // Stone Bricks
    shaped()
      .pattern("SS")
      .pattern("SS")
      .define('S', DataIngredient.of(Block.STONE.asItem()))
      .result(ItemStack(MyGoodMod.stoneBricksBlock, 4))
      .save("stone_bricks", this, ctx)
    shaped()
      .pattern("WWW")
      .pattern("W W")
      .pattern("WWW")
      .define('W', DataIngredient.of(Item.WHEAT))
      .result(ItemStack(MyGoodMod.bagelItem, 2))
      .save("bagel", this, ctx)
  }
}
