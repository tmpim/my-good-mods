package pw.tmpim.mygoodmod

import net.glasslauncher.mods.gcapi3.api.ConfigEntry

class MyGoodModConfig {
  @JvmField
  @ConfigEntry(
    name = "Boats drop boat item",
    description = "Boats drop the boat item instead of planks and sticks"
  )
  var boatsDropBoatItem: Boolean? = true
}
