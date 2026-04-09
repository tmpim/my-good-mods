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

  @JvmField
  @ConfigEntry(
    name = "Shorten item counts",
    nameKey = "$C.shorten_item_counts",
    description = "Abbreviate item counts above 1000 when displayed in the GUI",
    descriptionKey = "$C.shorten_item_counts.desc",
  )
  var shortenItemCounts: Boolean? = true

  @JvmField
  @ConfigEntry(
    name = "Negative item counts",
    nameKey = "$C.negative_item_counts",
    description = "Show negative item counts in the GUI (helpful for debugging)",
    descriptionKey = "$C.negative_item_counts.desc",
  )
  var negativeItemCounts: Boolean? = true

  @JvmField
  @ConfigEntry(
    name = "Debug",
    nameKey = "$C.debug",
    description = "If enabled, registry values and overrides will be printed to debug.log",
    descriptionKey = "$C.debug.desc",
  )
  var debug: Boolean? = false
}
