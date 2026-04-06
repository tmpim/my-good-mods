package pw.tmpim.goodfarming.data

import emmathemartian.datagen.DataGenContext
import emmathemartian.datagen.provider.ItemModelProvider

class GoodFarmingItemModelProvider(ctx: DataGenContext) : ItemModelProvider(ctx) {
  override fun run(ctx: DataGenContext) {
    // don't register an item model so we can use getTextureId without having to register an entire custom item renderer
    // simpleItem(GoodFarming.seedBag).save("seed_bag", this, ctx)
  }
}
