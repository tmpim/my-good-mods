package pw.tmpim.goodstacks

import net.glasslauncher.mods.gcapi3.api.ConfigEntry
import pw.tmpim.goodstacks.GoodStacks.MOD_ID

internal const val CONFIG_KEY = "gui.${MOD_ID}.config"
private const val C = CONFIG_KEY

class GoodStacksConfig {
  @JvmField
  @ConfigEntry(
    name = "Maximum stack size",
    nameKey = "$C.max_stack_size",
    description = "Overrides the maximum stack size for any item with the default stack size (64)",
    descriptionKey = "$C.max_stack_size.desc",
    minValue = 1.0,
    maxValue = Int.MAX_VALUE.toDouble(),
    multiplayerSynced = true
  )
  var maxStackSize: Int? = 999
}
