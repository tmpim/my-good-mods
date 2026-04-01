package pw.tmpim.goodmod.data

import emmathemartian.datagen.DataGenContext
import emmathemartian.datagen.provider.BlockStateProvider
import pw.tmpim.goodmod.data.GoodModData.namespace

class GoodBlockStateProvider(ctx: DataGenContext) : BlockStateProvider(ctx) {
  override fun run(ctx: DataGenContext) {
    variant()
      .variant<String>("", namespace.id("block/redstone_block"))
      .save("redstone_block", this, ctx)

    variant()
      .variant<String>("", namespace.id("block/stone_bricks"))
      .save("stone_bricks", this, ctx)

    variant()
      .variant<String>("", namespace.id("block/hay_block"))
      .save("hay_block", this, ctx)

    variant()
      .variant<String>("", namespace.id("block/coal_block"))
      .save("coal_block", this, ctx)
  }
}
