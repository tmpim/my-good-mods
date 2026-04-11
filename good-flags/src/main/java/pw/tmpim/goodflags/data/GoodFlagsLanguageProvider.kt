package pw.tmpim.goodflags.data

import emmathemartian.datagen.DataGenContext
import emmathemartian.datagen.builder.LangBuilder
import emmathemartian.datagen.provider.LanguageProvider
import pw.tmpim.goodflags.config.CONFIG_KEY
import pw.tmpim.goodflags.GoodFlags
import pw.tmpim.goodflags.GoodFlags.MOD_ID

object TranslationString {
  const val COLOR = "gui.$MOD_ID.color"
  const val TOOL = "gui.$MOD_ID.tool"
}

private const val TC = TranslationString.COLOR
private const val TT = TranslationString.TOOL
private const val C = CONFIG_KEY

class GoodFlagsLanguageProvider(ctx: DataGenContext) : LanguageProvider(ctx) {
  override fun run(ctx: DataGenContext) {
    LangBuilder()
      .add(GoodFlags.flagBlock, "Flag")
      .add(GoodFlags.flagPoleBlock, "Flag Pole")
      .add("gui.$MOD_ID.title", "Paint Flag")
      .addColors()
      .addTools()
      .addConfig()
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
    .add("$TT.circle", "Circle")

  private fun LangBuilder.addConfig() = this
    .add("$C.name", GoodFlags.MOD_NAME)
    .add("$C.item_renderer_enabled", "Show flag preview on item")
    .add("$C.item_renderer_enabled.desc", "Draw the flag's painted design on top of the flag item icon in inventory slots")
    .add("$C.item_texture_resolution", "Item texture base resolution")
    .add("$C.item_texture_resolution.desc", "Base resolution of the item icon in pixels (default 16 for standard textures; increase for higher-resolution texture packs)")
    .add("$C.flag_preview_x", "Flag preview X offset")
    .add("$C.flag_preview_x.desc", "X offset (in item pixels) of the flag preview area within the item icon")
    .add("$C.flag_preview_y", "Flag preview Y offset")
    .add("$C.flag_preview_y.desc", "Y offset (in item pixels) of the flag preview area within the item icon")
    .add("$C.flag_preview_width", "Flag preview width")
    .add("$C.flag_preview_width.desc", "Width (in item pixels) of the flag preview area within the item icon")
    .add("$C.flag_preview_height", "Flag preview height")
    .add("$C.flag_preview_height.desc", "Height (in item pixels) of the flag preview area within the item icon")
}
