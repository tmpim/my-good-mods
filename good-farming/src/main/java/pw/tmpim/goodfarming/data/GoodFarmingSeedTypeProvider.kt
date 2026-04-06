package pw.tmpim.goodfarming.data

import emmathemartian.datagen.DataGenContext
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import pw.tmpim.goodfarming.api.SeedTypeProvider

class GoodFarmingSeedTypeProvider(ctx: DataGenContext) : SeedTypeProvider(ctx) {
  override fun run(ctx: DataGenContext) {
    seedType()
      .item(Item.SEEDS)
      .textureName("wheat_seeds")
      .save("wheat_seeds", this, ctx)

    seedType()
      .item(ItemStack(Item.DYE, 1, 15 /* bone meal */))
      .textureName("bone_meal")
      .plantOnBlock(Block.FARMLAND)
      .save("bone_meal", this, ctx)
  }
}
