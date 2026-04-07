package pw.tmpim.goodfarming.data

import emmathemartian.datagen.DataGenContext
import emmathemartian.datagen.provider.BlockTagProvider
import net.minecraft.block.Block

class GoodFarmingBlockTagProvider(ctx: DataGenContext) : BlockTagProvider(ctx) {
  override fun run(ctx: DataGenContext) {
    tag()
      .add(Block.SAPLING)
      .save("saplings", this, ctx)

    tag()
      .add(Block.WHEAT)
      .save("crops", this, ctx)
  }
}
