package pw.tmpim.goodfarming

import net.glasslauncher.mods.gcapi3.api.ConfigEntry
import pw.tmpim.goodfarming.GoodFarming.MOD_ID

internal const val CONFIG_KEY = "gui.${MOD_ID}.config"
private const val C = CONFIG_KEY

class GoodFarmingConfig {
   @JvmField
   @ConfigEntry(
     name = "Quick replanting enabled",
     nameKey = "$C.quick_replanting_enabled",
     description = "Allow replanting crops with right-click (if you have the seeds)",
     descriptionKey = "$C.quick_replanting_enabled.desc",
   )
   var quickReplantingEnabled: Boolean? = true

   @JvmField
   @ConfigEntry(
     name = "Trampling nerf enabled",
     nameKey = "$C.trampling_nerf_enabled",
     description = "If enabled, farmland is only trampled when jumping, not walking",
     descriptionKey = "$C.trampling_nerf_enabled.desc",
   )
   var tramplingNerfEnabled: Boolean? = true

   @JvmField
   @ConfigEntry(
     name = "Seed Bag planting lateral radius",
     nameKey = "$C.seed_bag_plant_lateral_radius",
     description = "The lateral radius, in blocks, to plant seeds with the Seed Bag",
     descriptionKey = "$C.seed_bag_plant_lateral_radius.desc",
     minValue = 1.0,
     maxValue = 16.0,
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
   )
   var seedBagCapacity: Int? = 512
}
