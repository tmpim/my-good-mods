package pw.tmpim.goodmod.data

import emmathemartian.datagen.DataGenContext
import emmathemartian.datagen.provider.CraftingRecipeProvider
import emmathemartian.datagen.util.DataIngredient
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import pw.tmpim.goodmod.GoodMod

class GoodCraftingRecipeProvider(ctx: DataGenContext) : CraftingRecipeProvider(ctx) {
  override fun run(ctx: DataGenContext) {
    // Redstone Block
    shaped()
      .pattern("RRR")
      .pattern("RRR")
      .pattern("RRR")
      .define('R', DataIngredient.of(Item.REDSTONE))
      .result(ItemStack(GoodMod.redstoneBlock))
      .save("redstone_block", this, ctx)
    shapeless()
      .ingredient(DataIngredient.of(GoodMod.redstoneBlock.asItem()))
      .result(ItemStack(Item.REDSTONE, 9))
      .save("redstone", this, ctx)

    // Hay Bale
    shaped()
      .pattern("WWW")
      .pattern("WWW")
      .pattern("WWW")
      .define('W', DataIngredient.of(Item.WHEAT))
      .result(ItemStack(GoodMod.hayBlock))
      .save("hay_bale", this, ctx)
    shapeless()
      .ingredient(DataIngredient.of(GoodMod.hayBlock.asItem()))
      .result(ItemStack(Item.WHEAT, 9))
      .save("wheat", this, ctx)

    // Coal Block
    shaped()
      .pattern("CCC")
      .pattern("CCC")
      .pattern("CCC")
      .define('C', DataIngredient.of(Item.COAL))
      .result(ItemStack(GoodMod.coalBlock))
      .save("coal_block", this, ctx)
    shapeless()
      .ingredient(DataIngredient.of(GoodMod.coalBlock.asItem()))
      .result(ItemStack(Item.COAL, 9))
      .save("coal", this, ctx)

    // Stone Bricks
    shaped()
      .pattern("SS")
      .pattern("SS")
      .define('S', DataIngredient.of(Block.STONE.asItem()))
      .result(ItemStack(GoodMod.stoneBricksBlock, 4))
      .save("stone_bricks", this, ctx)

    // Bagel
    shaped()
      .pattern("WWW")
      .pattern("W W")
      .pattern("WWW")
      .define('W', DataIngredient.of(Item.WHEAT))
      .result(ItemStack(GoodMod.bagelItem, 2))
      .save("bagel", this, ctx)
  }
}
