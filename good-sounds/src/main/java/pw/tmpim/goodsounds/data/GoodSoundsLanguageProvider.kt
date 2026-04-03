package pw.tmpim.goodsounds.data

import emmathemartian.datagen.DataGenContext
import emmathemartian.datagen.builder.LangBuilder
import emmathemartian.datagen.provider.LanguageProvider
import pw.tmpim.goodsounds.CONFIG_KEY
import pw.tmpim.goodsounds.GoodSounds.MOD_NAME

private const val C = CONFIG_KEY

class GoodSoundsLanguageProvider(ctx: DataGenContext) : LanguageProvider(ctx) {
  override fun run(ctx: DataGenContext) {
    LangBuilder()
      .add("$C.name", MOD_NAME)
      .add("$C.metal_pipe_enabled", "Metal pipe")
      .add("$C.metal_pipe_enabled.desc", "Plays the metal pipe sound when hitting entities with an iron item.")
      .save("en_US", this, ctx)
  }
}
