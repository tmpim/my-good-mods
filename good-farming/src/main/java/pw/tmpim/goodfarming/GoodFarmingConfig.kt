package pw.tmpim.goodfarming

import net.glasslauncher.mods.gcapi3.api.ConfigEntry
import pw.tmpim.goodfarming.GoodFarming.MOD_ID

internal const val CONFIG_KEY = "gui.${MOD_ID}.config"
private const val C = CONFIG_KEY

class GoodFarmingConfig {
  @JvmField
  @ConfigEntry(
    name = "Trampling nerf enabled",
    nameKey = "$C.trampling_nerf_enabled",
    description = "If enabled, farmland is only trampled when jumping, not walking",
    descriptionKey = "$C.trampling_nerf_enabled.desc",
    multiplayerSynced = true,
  )
  var tramplingNerfEnabled: Boolean? = true

  @JvmField
  @ConfigEntry(
    name = "Prevent Bone Meal wastage on Wheat",
    nameKey = "$C.bonemeal_wastage_fix_enabled",
    description = "Prevents consuming Bone Meal when used on fully grown wheat and saplings that can't grow (doesn't affect modded blocks)",
    descriptionKey = "$C.bonemeal_wastage_fix_enabled.desc",
    multiplayerSynced = true,
  )
  var bonemealWastageFixEnabled: Boolean? = true

  @JvmField
  @ConfigEntry(
    name = "Quick replanting enabled",
    nameKey = "$C.quick_replanting_enabled",
    description = "Allow replanting crops with right-click (if you have the seeds)",
    descriptionKey = "$C.quick_replanting_enabled.desc",
    multiplayerSynced = true,
  )
  var quickReplantingEnabled: Boolean? = true

   @JvmField
   @ConfigEntry(
     name = "Seed Bag planting lateral radius",
     nameKey = "$C.seed_bag_plant_lateral_radius",
     description = "The lateral radius, in blocks, to plant seeds with the Seed Bag",
     descriptionKey = "$C.seed_bag_plant_lateral_radius.desc",
     minValue = 1.0,
     maxValue = 16.0,
     multiplayerSynced = true,
   )
   var seedBagPlantLateralRadius: Int? = 3

   @JvmField
   @ConfigEntry(
     name = "Seed Bag planting vertical radius",
     nameKey = "$C.seed_bag_plant_vertical_radius",
     description = "The vertical radius, in blocks, to plant seeds with the Seed Bag",
     descriptionKey = "$C.seed_bag_plant_vertical_radius.desc",
     minValue = 1.0,
     maxValue = 9.0,
     multiplayerSynced = true,
   )
   var seedBagPlantVerticalRadius: Int? = 2

   @JvmField
   @ConfigEntry(
     name = "Seed Bag throwing range",
     nameKey = "$C.seed_bag_throw_range",
     description = "The range, in blocks, that the Seed Bag can be used from",
     descriptionKey = "$C.seed_bag_throw_range.desc",
     minValue = 1.0,
     maxValue = 9.0,
     multiplayerSynced = true,
   )
   var seedBagThrowRange: Double? = 5.0

   @JvmField
   @ConfigEntry(
     name = "Seed Bag capacity",
     nameKey = "$C.seed_capacity",
     description = "The maximum number of seeds the Seed Bag can store",
     descriptionKey = "$C.seed_capacity.desc",
     minValue = 1.0,
     maxValue = Int.MAX_VALUE.toDouble(),
     multiplayerSynced = true,
   )
   var seedBagCapacity: Int? = 512

   @JvmField
   @ConfigEntry(
     name = "Seed Bag count overlay enabled",
     nameKey = "$C.seed_bag_overlay_enabled",
     description = "Displays the amount of seeds in the Seed Bag in the hotbar and inventory",
     descriptionKey = "$C.seed_bag_overlay_enabled.desc",
   )
   var seedBagOverlayEnabled: Boolean? = true
}
