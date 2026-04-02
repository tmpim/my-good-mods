package pw.tmpim.goodcompression.data

import emmathemartian.datagen.DataGenContext
import emmathemartian.datagen.provider.BlockTagProvider
import pw.tmpim.goodcompression.GoodCompression

class MinecraftTagProvider(ctx: DataGenContext) : BlockTagProvider(ctx) {
  override fun run(ctx: DataGenContext) {
    tag()
      .add(GoodCompression.redstoneBlock)
      .add(GoodCompression.coalBlock)
      .add(GoodCompression.stoneBricksBlock)
      .save("mineable/pickaxe", this, ctx)

    tag()
      .add(GoodCompression.hayBlock)
      .save("mineable/hoe", this, ctx)
  }
}
