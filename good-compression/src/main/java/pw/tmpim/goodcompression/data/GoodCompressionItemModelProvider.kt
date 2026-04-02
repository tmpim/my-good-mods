package pw.tmpim.goodcompression.data

import emmathemartian.datagen.DataGenContext
import emmathemartian.datagen.provider.ItemModelProvider
import pw.tmpim.goodcompression.GoodCompression

class GoodCompressionItemModelProvider(ctx: DataGenContext) : ItemModelProvider(ctx) {
  override fun run(ctx: DataGenContext) {
    blockItem(GoodCompression.redstoneBlock).save("redstone_block", this, ctx)
    blockItem(GoodCompression.stoneBricksBlock).save("stone_bricks", this, ctx)
    blockItem(GoodCompression.hayBlock).save("hay_block", this, ctx)
    blockItem(GoodCompression.coalBlock).save("coal_block", this, ctx)
  }
}
