package pw.tmpim.goodmod.data

import emmathemartian.datagen.DataGenContext
import emmathemartian.datagen.provider.BlockTagProvider
import pw.tmpim.goodmod.GoodMod

class MinecraftTagProvider(ctx: DataGenContext) : BlockTagProvider(ctx) {
  override fun run(ctx: DataGenContext) {
    tag()
      .add(GoodMod.redstoneBlock)
      .add(GoodMod.coalBlock)
      .add(GoodMod.stoneBricksBlock)
      .save("mineable/pickaxe", this, ctx)

    tag()
      .add(GoodMod.hayBlock)
      .save("mineable/hoe", this, ctx)
  }
}
