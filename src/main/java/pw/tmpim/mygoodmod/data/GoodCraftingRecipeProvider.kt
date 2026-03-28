package pw.tmpim.mygoodmod.data

import emmathemartian.datagen.DataGenContext
import emmathemartian.datagen.builder.LangBuilder
import emmathemartian.datagen.provider.BlockModelProvider
import emmathemartian.datagen.provider.CraftingRecipeProvider
import emmathemartian.datagen.provider.LanguageProvider
import emmathemartian.datagen.util.DataIngredient
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.modificationstation.stationapi.api.item.Items
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint
import net.modificationstation.stationapi.api.util.Namespace.MINECRAFT
import pw.tmpim.mygoodmod.MyGoodMod
import pw.tmpim.mygoodmod.MyGoodMod.MOD_ID
import pw.tmpim.mygoodmod.MyGoodMod.MOD_NAME

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
