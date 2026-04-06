package pw.tmpim.gooddeath.data

import emmathemartian.datagen.DataGenContext
import emmathemartian.datagen.provider.ItemModelProvider
import pw.tmpim.gooddeath.GoodDeath

class GoodDeathItemModelProvider(ctx: DataGenContext) : ItemModelProvider(ctx) {
  override fun run(ctx: DataGenContext?) {
    blockItem(GoodDeath.tombstoneBlock).save("tombstone", this, ctx)
  }
}
