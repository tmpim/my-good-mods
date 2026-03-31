package pw.tmpim.mygoodmod.data

import emmathemartian.datagen.DataGenContext
import emmathemartian.datagen.provider.ItemModelProvider
import pw.tmpim.mygoodmod.MyGoodMod

class GoodItemModelProvider(ctx: DataGenContext) : ItemModelProvider(ctx) {
  override fun run(ctx: DataGenContext) {
    blockItem(MyGoodMod.redstoneBlock).save("redstone_block", this, ctx)
    blockItem(MyGoodMod.stoneBricksBlock).save("stone_bricks", this, ctx)
    blockItem(MyGoodMod.hayBlock).save("hay_block", this, ctx)
    blockItem(MyGoodMod.coalBlock).save("coal_block", this, ctx)
  }
}
