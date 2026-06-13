package pw.tmpim.gooddeath.data

import emmathemartian.datagen.DataGenContext
import emmathemartian.datagen.entrypoint.DataEntrypoint
import net.modificationstation.stationapi.api.util.Namespace

object GoodDeathData : DataEntrypoint {
  val namespace: Namespace = Namespace.resolve()

  override fun run() {
    val ctx = DataGenContext(namespace)

    ctx.run(GoodDeathBlockStateProvider(ctx))
    ctx.run(GoodDeathItemModelProvider(ctx))
    ctx.run(GoodDeathCraftingRecipeProvider(ctx))
    ctx.run(GoodDeathLanguageProvider(ctx))
  }
}
