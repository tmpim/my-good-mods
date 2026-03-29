package pw.tmpim.mygoodmod.data

import emmathemartian.datagen.DataGenContext
import emmathemartian.datagen.provider.BlockTagProvider
import pw.tmpim.mygoodmod.MyGoodMod

class MinecraftTagProvider(ctx: DataGenContext) : BlockTagProvider(ctx) {
  override fun run(ctx: DataGenContext) {

    tag()
      .add(MyGoodMod.redstoneBlock)
      .save("mineable/pickaxe", this, ctx)
  }
}
