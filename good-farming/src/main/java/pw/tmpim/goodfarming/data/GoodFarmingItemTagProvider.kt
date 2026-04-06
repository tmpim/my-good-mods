package pw.tmpim.goodfarming.data

import emmathemartian.datagen.DataGenContext
import emmathemartian.datagen.provider.ItemTagProvider
import net.minecraft.item.Item

class GoodFarmingItemTagProvider(ctx: DataGenContext) : ItemTagProvider(ctx) {
  override fun run(ctx: DataGenContext) {
    tag()
      .add(Item.SEEDS)
      .add(Item.DYE) // bone meal, and any other dye for now i guess
      .save("seeds", this, ctx)
  }
}
