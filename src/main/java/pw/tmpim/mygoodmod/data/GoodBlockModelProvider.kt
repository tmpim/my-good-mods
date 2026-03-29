package pw.tmpim.mygoodmod.data

import emmathemartian.datagen.DataGenContext
import emmathemartian.datagen.provider.BlockModelProvider
import pw.tmpim.mygoodmod.data.MyGoodModData.namespace

class GoodBlockModelProvider(ctx: DataGenContext) : BlockModelProvider(ctx) {
  override fun run(ctx: DataGenContext) {
    cubeAll()
      .texture("all", namespace.id("block/redstone_block"))
      .save("redstone_block", this, ctx)
  }
}
