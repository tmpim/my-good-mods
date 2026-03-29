package pw.tmpim.mygoodmod.data

import emmathemartian.datagen.DataGenContext
import emmathemartian.datagen.provider.ItemModelProvider
import pw.tmpim.mygoodmod.MyGoodMod

class GoodItemModelProvider(ctx: DataGenContext) : ItemModelProvider(ctx) {
  override fun run(ctx: DataGenContext) {
    blockItem(MyGoodMod.redstoneBlock).save("redstone_block", this, ctx)
    blockItem(MyGoodMod.stoneBricksBlock).save("stone_bricks", this, ctx)
  }
}
