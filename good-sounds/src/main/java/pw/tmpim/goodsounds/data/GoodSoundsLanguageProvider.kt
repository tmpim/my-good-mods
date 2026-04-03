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
      .add("$C.metal_pipe_on_hit_enabled", "Metal pipe")
      .add("$C.metal_pipe_on_hit_enabled.desc", "Plays the metal pipe sound when hitting entities with an iron item.")
      .add("$C.rain_volume", "Rain volume")
      .add("$C.rain_volume.desc", "How loud the rain is, as a fraction of the vanilla volume (0.00-1.00).")
      .add("$C.metal_pipe_volume", "Metal pipe volume")
      .add("$C.metal_pipe_volume.desc", "Volume of the metal pipe sound (0.00-1.00).")
      .add("$C.metal_pipe_on_fall_enabled", "Metal pipe on fall damage")
      .add("$C.metal_pipe_on_fall_enabled.desc", "Plays the metal pipe when taking fall damage with iron boots.")
      .save("en_US", this, ctx)
  }
}
