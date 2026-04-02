package pw.tmpim.goodcompression

import net.glasslauncher.mods.gcapi3.api.ConfigEntry
import pw.tmpim.goodcompression.GoodCompression.MOD_ID

internal const val CONFIG_KEY = "gui.${MOD_ID}.config"
private const val C = CONFIG_KEY

class GoodModConfig {
  @JvmField
  @ConfigEntry(
    name = "Redstone dust on top of blocks",
    nameKey = "$C.redstone_dust_on_top_of_blocks",
    description = "Allow redstone dust to be placed on non-solid blocks, such as Redstone Blocks, if they are in the "
      + "good-compression:redstone_dust_placeable tag.",
    descriptionKey = "$C.redstone_dust_on_top_of_blocks.desc",
  )
  var redstoneDustOnTopOfBlocks: Boolean? = true
}
