package pw.tmpim.goodconfig.data

import emmathemartian.datagen.DataGenContext
import emmathemartian.datagen.builder.LangBuilder
import emmathemartian.datagen.provider.LanguageProvider

class GoodConfigLanguageProvider(ctx: DataGenContext) : LanguageProvider(ctx) {
  override fun run(ctx: DataGenContext) {
    LangBuilder()
      // .add("$C.name", MOD_NAME)
      // .add("$C.drop_item_enabled", "Boats drop boat item")
      // .add("$C.drop_item_enabled.desc", "Boats drop the boat item instead of planks and sticks")
      .save("en_US", this, ctx)
  }
}
