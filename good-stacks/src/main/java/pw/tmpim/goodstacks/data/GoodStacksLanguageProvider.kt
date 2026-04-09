package pw.tmpim.goodstacks.data

import emmathemartian.datagen.DataGenContext
import emmathemartian.datagen.builder.LangBuilder
import emmathemartian.datagen.provider.LanguageProvider
import pw.tmpim.goodstacks.CONFIG_KEY
import pw.tmpim.goodstacks.GoodStacks.MOD_NAME

private const val C = CONFIG_KEY

class GoodStacksLanguageProvider(ctx: DataGenContext) : LanguageProvider(ctx) {
  override fun run(ctx: DataGenContext) {
    LangBuilder()
      .add("$C.name", MOD_NAME)
      .add("$C.max_stack_size", "Maximum stack size")
      .add("$C.max_stack_size.desc", "Overrides the maximum stack size for any item with the default stack size (64)")
      .add("$C.shorten_item_counts", "Shorten item counts")
      .add("$C.shorten_item_counts.desc", "Abbreviate item counts above 1000 when displayed in the GUI")
      .add("$C.negative_item_counts", "Negative item counts")
      .add("$C.negative_item_counts.desc", "Show negative item counts in the GUI (helpful for debugging)")
      .add("$C.debug", "Debug")
      .add("$C.debug.desc", "If enabled, registry values and overrides will be printed to debug.log")
      .save("en_US", this, ctx)
  }
}
