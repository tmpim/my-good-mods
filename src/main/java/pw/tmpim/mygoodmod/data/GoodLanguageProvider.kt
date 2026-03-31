package pw.tmpim.mygoodmod.data

import emmathemartian.datagen.DataGenContext
import emmathemartian.datagen.builder.LangBuilder
import emmathemartian.datagen.provider.LanguageProvider
import pw.tmpim.mygoodmod.MyGoodMod
import pw.tmpim.mygoodmod.MyGoodMod.MOD_ID
import pw.tmpim.mygoodmod.MyGoodMod.MOD_NAME

class GoodLanguageProvider(ctx: DataGenContext) : LanguageProvider(ctx) {
  override fun run(ctx: DataGenContext) {
    LangBuilder()
      .add("gui.$MOD_ID.config.name", MOD_NAME)
      .add(MyGoodMod.redstoneBlock, "Block of Redstone")
      .add(MyGoodMod.stoneBricksBlock, "Stone Bricks")
      .add(MyGoodMod.hayBlock, "Hay Bale")
      .add(MyGoodMod.coalBlock, "Block of Coal")
      .save("en_US", this, ctx)
  }
}
