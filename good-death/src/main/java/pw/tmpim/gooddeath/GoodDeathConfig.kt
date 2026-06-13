package pw.tmpim.gooddeath

import pw.tmpim.gooddeath.GoodDeath.MOD_ID

internal const val CONFIG_KEY = "gui.${MOD_ID}.config"
private const val C = CONFIG_KEY

// NB: all java primitives must be boxed nullables in kotlin, with @JvmField
class GoodDeathConfig {
  // @JvmField
  // @ConfigEntry(
  //   name = "Boats drop boat item",
  //   nameKey = "$C.drop_item_enabled",
  //   description = "Boats drop the boat item instead of planks and sticks",
  //   descriptionKey = "$C.drop_item_enabled.desc",
  // )
  // var boatsDropBoatItem: Boolean? = true
}
