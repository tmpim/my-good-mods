package pw.tmpim.goodflags.data

import emmathemartian.datagen.DataGenContext
import emmathemartian.datagen.provider.BlockTagProvider
import pw.tmpim.goodflags.GoodFlags

class MinecraftTagProvider(ctx: DataGenContext) : BlockTagProvider(ctx) {
  override fun run(ctx: DataGenContext) {
    tag()
      .add(GoodFlags.flagBlock)
      .add(GoodFlags.flagPoleBlock)
      .save("mineable/axe", this, ctx)
  }
}
