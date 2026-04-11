package pw.tmpim.goodflags.config

import net.glasslauncher.mods.gcapi3.api.ConfigEntry
import pw.tmpim.goodflags.GoodFlags.MOD_ID

internal const val CONFIG_KEY = "gui.${MOD_ID}.config"
private const val C = CONFIG_KEY

class GoodFlagsConfig {
  @JvmField
  @ConfigEntry(
    name = "Show flag preview on item",
    nameKey = "$C.item_renderer_enabled",
    description = "Draw the flag's painted design on top of the flag item icon in inventory slots",
    descriptionKey = "$C.item_renderer_enabled.desc",
  )
  var itemRendererEnabled: Boolean? = true

  @JvmField
  @ConfigEntry(
    name = "Item texture base resolution",
    nameKey = "$C.item_texture_resolution",
    description = "Base resolution of the item icon in pixels (default 16 for standard textures; increase for higher-resolution texture packs)",
    descriptionKey = "$C.item_texture_resolution.desc",
    minValue = 1.0,
    maxValue = 512.0,
  )
  var itemTextureResolution: Int? = 16

  @JvmField
  @ConfigEntry(
    name = "Flag preview X offset",
    nameKey = "$C.flag_preview_x",
    description = "X offset (in item pixels) of the flag preview area within the item icon",
    descriptionKey = "$C.flag_preview_x.desc",
    minValue = 0.0,
    maxValue = 512.0,
  )
  var flagPreviewX: Int? = 3

  @JvmField
  @ConfigEntry(
    name = "Flag preview Y offset",
    nameKey = "$C.flag_preview_y",
    description = "Y offset (in item pixels) of the flag preview area within the item icon",
    descriptionKey = "$C.flag_preview_y.desc",
    minValue = 0.0,
    maxValue = 512.0,
  )
  var flagPreviewY: Int? = 0

  @JvmField
  @ConfigEntry(
    name = "Flag preview width",
    nameKey = "$C.flag_preview_width",
    description = "Width (in item pixels) of the flag preview area within the item icon",
    descriptionKey = "$C.flag_preview_width.desc",
    minValue = 1.0,
    maxValue = 512.0,
  )
  var flagPreviewWidth: Int? = 12

  @JvmField
  @ConfigEntry(
    name = "Flag preview height",
    nameKey = "$C.flag_preview_height",
    description = "Height (in item pixels) of the flag preview area within the item icon",
    descriptionKey = "$C.flag_preview_height.desc",
    minValue = 1.0,
    maxValue = 512.0,
  )
  var flagPreviewHeight: Int? = 8
}
