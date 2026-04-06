package pw.tmpim.gooddeath.data

import emmathemartian.datagen.DataGenContext
import emmathemartian.datagen.builder.LangBuilder
import emmathemartian.datagen.provider.LanguageProvider
import pw.tmpim.gooddeath.CONFIG_KEY
import pw.tmpim.gooddeath.GoodDeath
import pw.tmpim.gooddeath.GoodDeath.MOD_NAME

private const val C = CONFIG_KEY

class GoodDeathLanguageProvider(ctx: DataGenContext) : LanguageProvider(ctx) {
  override fun run(ctx: DataGenContext) {
    LangBuilder()
      .add("$C.name", MOD_NAME)
      .add(GoodDeath.tombstoneBlock, "Tombstone")
      .save("en_US", this, ctx)

    LangBuilder()
      .add("$C.name", "Guter Tod")
      .add(GoodDeath.tombstoneBlock, "Grabstein")
      .save("de_DE", this, ctx)
  }
}
