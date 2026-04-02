package pw.tmpim.goodmod.data

import emmathemartian.datagen.DataGenContext
import emmathemartian.datagen.builder.LangBuilder
import emmathemartian.datagen.provider.LanguageProvider
import pw.tmpim.goodmod.CONFIG_KEY
import pw.tmpim.goodmod.GoodMod.MOD_NAME

private const val C = CONFIG_KEY

class GoodLanguageProvider(ctx: DataGenContext) : LanguageProvider(ctx) {
  override fun run(ctx: DataGenContext) {
    LangBuilder()
      .add("$C.name", MOD_NAME)
      .save("en_US", this, ctx)
  }
}
