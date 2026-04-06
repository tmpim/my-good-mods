package pw.tmpim.goodutils

import pw.tmpim.goodutils.GoodUtils.MOD_ID

internal const val CONFIG_KEY = "gui.${MOD_ID}.config"
private const val C = CONFIG_KEY

// NB: all java primitives must be boxed nullables in kotlin, with @JvmField
class GoodUtilsConfig {
  // @JvmField
  // @ConfigEntry(
  //   name = "Boats drop boat item",
  //   nameKey = "$C.drop_item_enabled",
  //   description = "Boats drop the boat item instead of planks and sticks",
  //   descriptionKey = "$C.drop_item_enabled.desc",
  // )
  // var boatsDropBoatItem: Boolean? = true
}
