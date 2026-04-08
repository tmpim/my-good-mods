package pw.tmpim.goodfarming.data

import emmathemartian.datagen.DataGenContext
import net.minecraft.block.Block
import net.minecraft.item.Item
import pw.tmpim.goodfarming.api.CropTypeProvider

class GoodFarmingCropTypeProvider(ctx: DataGenContext) : CropTypeProvider(ctx) {
  override fun run(ctx: DataGenContext) {
    cropType()
      .crop(Block.WHEAT, 7 /* fully grown wheat only */)
      .seed(Item.SEEDS)
      .save("wheat", this, ctx)
  }
}
