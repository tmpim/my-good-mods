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
      .pattern("SSS")
      .pattern("SBS")
      .pattern("T_T")
      .define('S', DataIngredient.of(Block.STONE.asItem()))
      .define('B', DataIngredient.of(Item.BONE))
      .define('T', DataIngredient.of(Block.TORCH.asItem()))
      .define('_', DataIngredient.of(Block.SLAB.asItem()))
      .result(ItemStack(GoodDeath.tombstoneBlock))
      .save("tombstone", this, ctx)
  }
}
