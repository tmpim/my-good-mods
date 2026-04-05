package pw.tmpim.goodclumps.data

import emmathemartian.datagen.DataGenContext
import emmathemartian.datagen.builder.LangBuilder
import emmathemartian.datagen.provider.LanguageProvider
import pw.tmpim.goodclumps.CONFIG_KEY
import pw.tmpim.goodclumps.GoodClumps.MOD_NAME

private const val C = CONFIG_KEY

class GoodClumpsLanguageProvider(ctx: DataGenContext) : LanguageProvider(ctx) {
  override fun run(ctx: DataGenContext) {
    LangBuilder()
      .add("$C.name", MOD_NAME)
      .add("$C.item_merge_enabled", "Item merging enabled")
      .add("$C.item_merge_radius", "Item merge radius")
      .add("$C.item_merge_radius.desc", "The radius, in blocks, of item merging")
      .add("$C.item_merge_rate_static", "Item merge rate when static")
      .add("$C.item_merge_rate_static.desc", "How frequently, in ticks, dropped items should try to merge with " +
        "others nearby when still")
      .add("$C.item_merge_rate_moving", "Item merge rate when moving")
      .add("$C.item_merge_rate_moving.desc", "How frequently, in ticks, dropped items should try to merge with " +
        "others nearby when moving")
      .save("en_US", this, ctx)
  }
}
