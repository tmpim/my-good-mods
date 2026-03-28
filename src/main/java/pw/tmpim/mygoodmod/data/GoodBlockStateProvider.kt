package pw.tmpim.mygoodmod.data

import emmathemartian.datagen.DataGenContext
import emmathemartian.datagen.provider.BlockStateProvider
import pw.tmpim.mygoodmod.data.MyGoodModData.namespace

class GoodBlockStateProvider(ctx: DataGenContext) : BlockStateProvider(ctx) {
  override fun run(ctx: DataGenContext) {
    variant()
      .variant<String>("", namespace.id("block/redstone_block"))
      .save("redstone_block", this, ctx)
  }
}
