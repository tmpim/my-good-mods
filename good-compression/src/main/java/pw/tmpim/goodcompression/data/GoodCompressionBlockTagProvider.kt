package pw.tmpim.goodcompression.data

import emmathemartian.datagen.DataGenContext
import emmathemartian.datagen.provider.BlockTagProvider
import pw.tmpim.goodcompression.GoodCompression

class GoodCompressionBlockTagProvider(ctx: DataGenContext) : BlockTagProvider(ctx) {
  override fun run(ctx: DataGenContext) {
    tag()
      .add(GoodCompression.redstoneBlock)
      .save("redstone_dust_placeable", this, ctx)
  }
}
