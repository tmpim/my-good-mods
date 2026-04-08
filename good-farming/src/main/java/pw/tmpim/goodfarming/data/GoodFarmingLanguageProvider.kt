package pw.tmpim.goodfarming.data

import emmathemartian.datagen.DataGenContext
import emmathemartian.datagen.builder.LangBuilder
import emmathemartian.datagen.provider.LanguageProvider
import pw.tmpim.goodfarming.GoodFarming
import pw.tmpim.goodfarming.GoodFarming.MOD_NAME
import pw.tmpim.goodfarming.config.CONFIG_KEY
import pw.tmpim.goodutils.i18n.sub

private const val C = CONFIG_KEY

class GoodFarmingLanguageProvider(ctx: DataGenContext) : LanguageProvider(ctx) {
  override fun run(ctx: DataGenContext) {
    LangBuilder()
      .add("$C.name", MOD_NAME)
      .add("$C.quick_replanting_enabled", "Quick replanting enabled")
      .add("$C.quick_replanting_enabled.desc", "Allow replanting crops with right-click (if you have the seeds)")
      .add("$C.bonemeal_wastage_fix_enabled", "Prevent Bone Meal wastage on Wheat")
      .add("$C.bonemeal_wastage_fix_enabled.desc", "Prevents consuming Bone Meal when used on fully grown wheat (doesn't affect modded blocks)")
      .add("$C.trampling_nerf_enabled", "Trampling nerf enabled")
      .add("$C.trampling_nerf_enabled.desc", "If enabled, farmland is only trampled when jumping, not walking")
      .add("$C.seed_bag_plant_lateral_radius", "Seed Bag planting lateral radius")
      .add("$C.seed_bag_plant_lateral_radius.desc", "The lateral radius, in blocks, to plant seeds with the Seed Bag")
      .add("$C.seed_bag_plant_vertical_radius", "Seed Bag planting vertical radius")
      .add("$C.seed_bag_plant_vertical_radius.desc", "The vertical radius, in blocks, to plant seeds with the Seed Bag")
      .add("$C.seed_bag_throw_range", "Seed Bag throwing range")
      .add("$C.seed_bag_throw_range.desc", "The range, in blocks, that the Seed Bag can be used from")
      .add("$C.seed_capacity", "Seed Bag capacity")
      .add("$C.seed_capacity.desc", "The maximum number of seeds the Seed Bag can store")
      .add("$C.seed_bag_auto_pickup_enabled", "Seed Bag auto pickup enabled")
      .add("$C.seed_bag_auto_pickup_enabled.desc", "Automatically put seeds into Seed Bags in your inventory when picked up")
      .add("$C.seed_bag_overlay_enabled", "Seed Bag count overlay enabled")
      .add("$C.seed_bag_overlay_enabled.desc", "Displays the amount of seeds in the Seed Bag in the hotbar and inventory")
      .add(GoodFarming.seedBag, "Seed Bag")
      .sub(GoodFarming.seedBag, "tooltip.seeds", "Contents: %,dx %s")
      .sub(GoodFarming.seedBag, "tooltip.unknown", "Unknown")
      .save("en_US", this, ctx)
  }
}
