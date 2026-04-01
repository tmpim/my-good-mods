package pw.tmpim.goodmod.data

import emmathemartian.datagen.DataGenContext
import emmathemartian.datagen.provider.BlockTagProvider
import pw.tmpim.goodmod.GoodMod

class GoodTagProvider(ctx: DataGenContext) : BlockTagProvider(ctx) {
  override fun run(ctx: DataGenContext) {
    tag()
      .add(GoodMod.redstoneBlock)
      .save("redstone_dust_placeable", this, ctx)
  }
}
