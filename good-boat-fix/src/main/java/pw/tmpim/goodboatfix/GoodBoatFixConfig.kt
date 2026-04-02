package pw.tmpim.goodboatfix

import net.glasslauncher.mods.gcapi3.api.ConfigEntry
import pw.tmpim.goodboatfix.GoodBoatFix.MOD_ID

internal const val CONFIG_KEY = "gui.${MOD_ID}.config"
private const val C = CONFIG_KEY

class GoodBoatFixConfig {
  @JvmField
  @ConfigEntry(
    name = "Boats drop boat item",
    nameKey = "$C.drop_item_enabled",
    description = "Boats drop the boat item instead of planks and sticks",
    descriptionKey = "$C.drop_item_enabled.desc",
  )
  var boatsDropBoatItem: Boolean? = true
}
