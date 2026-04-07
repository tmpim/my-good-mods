package pw.tmpim.goodfarming.data

import emmathemartian.datagen.DataGenContext
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import pw.tmpim.goodfarming.GoodFarming
import pw.tmpim.goodfarming.api.SeedTypeProvider
import pw.tmpim.goodfarming.data.GoodFarmingData.namespace

class GoodFarmingSeedTypeProvider(ctx: DataGenContext) : SeedTypeProvider(ctx) {
  override fun run(ctx: DataGenContext) {
    seedType()
      .item(Item.SEEDS)
      .textureId(namespace.id("item/seed_bag_wheat_seeds"))
      .save("wheat_seeds", this, ctx)

    seedType()
      .item(ItemStack(Item.DYE, 1, 15 /* bone meal */))
      .textureId(namespace.id("item/seed_bag_bone_meal"))
      .plantOnBlock(Block.WHEAT)
      .plantOnBlock(Block.SAPLING)
      .plantOnBlock(GoodFarming.crops)
      .plantOnBlock(GoodFarming.saplings)
      .save("bone_meal", this, ctx)
  }
}
