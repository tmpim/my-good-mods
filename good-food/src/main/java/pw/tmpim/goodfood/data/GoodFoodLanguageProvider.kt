package pw.tmpim.goodfood.data

import emmathemartian.datagen.DataGenContext
import emmathemartian.datagen.builder.LangBuilder
import emmathemartian.datagen.provider.LanguageProvider
import pw.tmpim.goodfood.CONFIG_KEY
import pw.tmpim.goodfood.GoodFood
import pw.tmpim.goodfood.GoodFood.MOD_NAME

private const val C = CONFIG_KEY

class GoodFoodLanguageProvider(ctx: DataGenContext) : LanguageProvider(ctx) {
  override fun run(ctx: DataGenContext) {
    LangBuilder()
      .add("$C.name", MOD_NAME)
      .add(GoodFood.bagelItem, "Bagel")
      .save("en_US", this, ctx)
  }
}
