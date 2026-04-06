package pw.tmpim.goodflags.data

import emmathemartian.datagen.DataGenContext
import emmathemartian.datagen.builder.LangBuilder
import emmathemartian.datagen.provider.LanguageProvider
import pw.tmpim.goodflags.GoodFlags
import pw.tmpim.goodflags.GoodFlags.MOD_ID

object TranslationString {
  internal const val COLOR = "gui.$MOD_ID.color"
  internal const val TOOL = "gui.$MOD_ID.tool"
}

private const val TC = TranslationString.COLOR
private const val TT = TranslationString.TOOL

class GoodFlagsLanguageProvider(ctx: DataGenContext) : LanguageProvider(ctx) {
  override fun run(ctx: DataGenContext) {
    LangBuilder()
      .add(GoodFlags.flagBlock, "Flag")
      .add(GoodFlags.flagPoleBlock, "Flag Pole")
      .add("gui.$MOD_ID.title", "Paint Flag")
      .addColors()
      .addTools()
      .save("en_US", this, ctx)
  }

  private fun LangBuilder.addColors() = this
    .add("$TC.black", "Black")
    .add("$TC.red", "Red")
    .add("$TC.green", "Green")
    .add("$TC.brown", "Brown")
    .add("$TC.blue", "Blue")
    .add("$TC.purple", "Purple")
    .add("$TC.cyan", "Cyan")
    .add("$TC.silver", "Light Gray")
    .add("$TC.gray", "Gray")
    .add("$TC.pink", "Pink")
    .add("$TC.lime", "Lime")
    .add("$TC.yellow", "Yellow")
    .add("$TC.lightBlue", "Light Blue")
    .add("$TC.magenta", "Magenta")
    .add("$TC.orange", "Orange")
    .add("$TC.white", "White")

  private fun LangBuilder.addTools() = this
    .add("$TT.title", "Tools")
    .add("$TT.brush", "Brush")
    .add("$TT.pencil", "Pencil")
    .add("$TT.fill", "Fill")
    .add("$TT.eraser", "Eraser")
    .add("$TT.line", "Line")
    .add("$TT.rect", "Rect")
}
