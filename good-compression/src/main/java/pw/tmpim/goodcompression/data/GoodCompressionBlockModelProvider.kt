package pw.tmpim.goodcompression.data

import emmathemartian.datagen.DataGenContext
import emmathemartian.datagen.provider.BlockModelProvider
import pw.tmpim.goodcompression.data.GoodCompressionData.namespace

class GoodCompressionBlockModelProvider(ctx: DataGenContext) : BlockModelProvider(ctx) {
  override fun run(ctx: DataGenContext) {
    cubeAll()
      .texture("all", namespace.id("block/redstone_block"))
      .save("redstone_block", this, ctx)

    cubeAll()
      .texture("all", namespace.id("block/stonebrick"))
      .save("stone_bricks", this, ctx)

    cubeColumn()
      .texture("end", namespace.id("block/hay_block_top"))
      .texture("side", namespace.id("block/hay_block_side"))
      .save("hay_block", this, ctx)

    cubeAll()
      .texture("all", namespace.id("block/coal_block"))
      .save("coal_block", this, ctx)
  }
}
