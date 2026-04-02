package pw.tmpim.goodfood.data

import emmathemartian.datagen.DataGenContext
import emmathemartian.datagen.entrypoint.DataEntrypoint
import net.modificationstation.stationapi.api.util.Namespace

object GoodFoodData : DataEntrypoint {
  val namespace: Namespace = Namespace.resolve()

  override fun run() {
    val ctx = DataGenContext(namespace)

    ctx.run(GoodFoodCraftingRecipeProvider(ctx))
    ctx.run(GoodFoodItemModelProvider(ctx))
    ctx.run(GoodFoodLanguageProvider(ctx))
  }
}
