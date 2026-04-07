package pw.tmpim.goodfarming.data

import emmathemartian.datagen.DataGenContext
import emmathemartian.datagen.entrypoint.DataEntrypoint
import net.modificationstation.stationapi.api.util.Namespace

object GoodFarmingData : DataEntrypoint {
  val namespace: Namespace = Namespace.resolve()

  override fun run() {
    val ctx = DataGenContext(namespace)

    ctx.run(GoodFarmingBlockTagProvider(ctx))
    ctx.run(GoodFarmingCraftingRecipeProvider(ctx))
    ctx.run(GoodFarmingItemModelProvider(ctx))
    ctx.run(GoodFarmingItemTagProvider(ctx))
    ctx.run(GoodFarmingLanguageProvider(ctx))

    ctx.run(GoodFarmingSeedTypeProvider(ctx))
  }
}
