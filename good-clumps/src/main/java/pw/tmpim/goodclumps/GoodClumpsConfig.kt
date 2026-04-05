package pw.tmpim.goodclumps

import net.glasslauncher.mods.gcapi3.api.ConfigEntry
import pw.tmpim.goodclumps.GoodClumps.MOD_ID

internal const val CONFIG_KEY = "gui.${MOD_ID}.config"
private const val C = CONFIG_KEY

class GoodClumpsConfig {
  @JvmField
  @ConfigEntry(
    name = "Item merging enabled",
    nameKey = "$C.item_merge_enabled",
  )
  var itemMergeEnabled: Boolean? = true

  @JvmField
  @ConfigEntry(
    name = "Item merge radius",
    nameKey = "$C.item_merge_radius",
    description = "The radius, in blocks, of item merging",
    descriptionKey = "$C.item_merge_radius.desc",
    minValue = 0.1,
    maxValue = 5.0,
  )
  var itemMergeRadius: Double? = 0.5

  @JvmField
  @ConfigEntry(
    name = "Item merge rate when static",
    nameKey = "$C.item_merge_rate_static",
    description = "How frequently, in ticks, dropped items should try to merge with others nearby when still",
    descriptionKey = "$C.item_merge_rate_static.desc",
    minValue = 1.0,
    maxValue = 100.0,
  )
  var itemMergeRateStatic: Int? = 40

  @JvmField
  @ConfigEntry(
    name = "Item merge rate when moving",
    nameKey = "$C.item_merge_rate_moving",
    description = "How frequently, in ticks, dropped items should try to merge with others nearby when moving",
    descriptionKey = "$C.item_merge_rate_moving.desc",
    minValue = 1.0,
    maxValue = 100.0,
  )
  var itemMergeRateMoving: Int? = 2
}
