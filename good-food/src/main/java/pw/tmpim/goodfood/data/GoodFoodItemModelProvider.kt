package pw.tmpim.goodfood.data

import emmathemartian.datagen.DataGenContext
import emmathemartian.datagen.provider.ItemModelProvider
import pw.tmpim.goodfood.GoodFood

class GoodFoodItemModelProvider(ctx: DataGenContext) : ItemModelProvider(ctx) {
  override fun run(ctx: DataGenContext) {
    simpleItem(GoodFood.bagelItem).save("bagel", this, ctx)
  }
}
