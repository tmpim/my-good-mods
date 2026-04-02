package pw.tmpim.goodcommandfixes.data

import emmathemartian.datagen.DataGenContext
import emmathemartian.datagen.builder.LangBuilder
import emmathemartian.datagen.provider.LanguageProvider
import pw.tmpim.goodcommandfixes.CONFIG_KEY
import pw.tmpim.goodcommandfixes.GoodCommandFixes.MOD_NAME

private const val C = CONFIG_KEY

class GoodCommandFixesLanguageProvider(ctx: DataGenContext) : LanguageProvider(ctx) {
  override fun run(ctx: DataGenContext) {
    LangBuilder()
      .add("$C.name", MOD_NAME)
      .add("$C.global_list_enabled", "Allow all players to run /list")
      .add("$C.tell_feedback_enabled", "Show /tell feedback to senders")
      .save("en_US", this, ctx)
  }
}
