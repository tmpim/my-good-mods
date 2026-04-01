package pw.tmpim.goodmod.data

import emmathemartian.datagen.DataGenContext
import emmathemartian.datagen.provider.ItemModelProvider
import pw.tmpim.goodmod.GoodMod

class GoodItemModelProvider(ctx: DataGenContext) : ItemModelProvider(ctx) {
  override fun run(ctx: DataGenContext) {
    blockItem(GoodMod.redstoneBlock).save("redstone_block", this, ctx)
    blockItem(GoodMod.stoneBricksBlock).save("stone_bricks", this, ctx)
    blockItem(GoodMod.hayBlock).save("hay_block", this, ctx)
    blockItem(GoodMod.coalBlock).save("coal_block", this, ctx)
    simpleItem(GoodMod.bagelItem).save("bagel", this, ctx)
  }
}
