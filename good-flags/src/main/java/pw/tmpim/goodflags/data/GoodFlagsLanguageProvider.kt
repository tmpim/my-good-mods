package pw.tmpim.goodflags.data

import emmathemartian.datagen.DataGenContext
import emmathemartian.datagen.builder.LangBuilder
import emmathemartian.datagen.provider.LanguageProvider
import pw.tmpim.goodflags.CONFIG_KEY
import pw.tmpim.goodflags.GoodFlags
import pw.tmpim.goodflags.GoodFlags.MOD_ID
import pw.tmpim.goodflags.GoodFlags.MOD_NAME

private const val C = CONFIG_KEY

object TranslationString {
  internal const val COLOR = "$MOD_ID.color"
}

private const val TC = TranslationString.COLOR

class GoodFlagsLanguageProvider(ctx: DataGenContext) : LanguageProvider(ctx) {
  override fun run(ctx: DataGenContext) {
    LangBuilder()
      .add("$C.name", MOD_NAME)
      .add(GoodFlags.flagBlock, "Flag")
      .addColors()
      .save("en_US", this, ctx)
  }

  private fun LangBuilder.addColors() = this
    .add("$TC.black.name", "Black")
    .add("$TC.red.name", "Red")
    .add("$TC.green.name", "Green")
    .add("$TC.brown.name", "Brown")
    .add("$TC.blue.name", "Blue")
    .add("$TC.purple.name", "Purple")
    .add("$TC.cyan.name", "Cyan")
    .add("$TC.silver.name", "Light Gray")
    .add("$TC.gray.name", "Gray")
    .add("$TC.pink.name", "Pink")
    .add("$TC.lime.name", "Lime")
    .add("$TC.yellow.name", "Yellow")
    .add("$TC.lightBlue.name", "Light Blue")
    .add("$TC.magenta.name", "Magenta")
    .add("$TC.orange.name", "Orange")
    .add("$TC.white.name", "White")
}
