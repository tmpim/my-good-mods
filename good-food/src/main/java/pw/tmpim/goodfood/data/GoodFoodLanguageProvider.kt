package pw.tmpim.goodfood.data

import emmathemartian.datagen.DataGenContext
import emmathemartian.datagen.builder.LangBuilder
import emmathemartian.datagen.provider.LanguageProvider
import pw.tmpim.goodfood.GoodFood

class GoodFoodLanguageProvider(ctx: DataGenContext) : LanguageProvider(ctx) {
  override fun run(ctx: DataGenContext) {
    LangBuilder()
      .add(GoodFood.bagelItem, "Bagel")
      .save("en_US", this, ctx)
  }
}
