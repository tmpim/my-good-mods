package pw.tmpim.goodmod.data

import emmathemartian.datagen.DataGenContext
import emmathemartian.datagen.builder.LangBuilder
import emmathemartian.datagen.provider.LanguageProvider
import pw.tmpim.goodmod.GoodMod
import pw.tmpim.goodmod.GoodMod.MOD_ID
import pw.tmpim.goodmod.GoodMod.MOD_NAME

class GoodLanguageProvider(ctx: DataGenContext) : LanguageProvider(ctx) {
  override fun run(ctx: DataGenContext) {
    LangBuilder()
      .add("gui.$MOD_ID.config.name", MOD_NAME)
      .add(GoodMod.redstoneBlock, "Block of Redstone")
      .add(GoodMod.stoneBricksBlock, "Stone Bricks")
      .add(GoodMod.hayBlock, "Hay Bale")
      .add(GoodMod.coalBlock, "Block of Coal")
      .add(GoodMod.bagelItem, "Bagel")
      .save("en_US", this, ctx)
  }
}
