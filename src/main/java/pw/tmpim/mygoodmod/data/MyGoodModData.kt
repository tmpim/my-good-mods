package pw.tmpim.mygoodmod.data

import emmathemartian.datagen.DataGenContext
import emmathemartian.datagen.entrypoint.DataEntrypoint
import net.modificationstation.stationapi.api.util.Namespace

object MyGoodModData : DataEntrypoint {
  val namespace: Namespace = Namespace.resolve()

  override fun run() {
    val ctx = DataGenContext(namespace)

    ctx.run(GoodBlockModelProvider(ctx))
    ctx.run(GoodBlockStateProvider(ctx))
    ctx.run(GoodCraftingRecipeProvider(ctx))
    ctx.run(GoodItemModelProvider(ctx))
    ctx.run(GoodLangaugeProvider(ctx))
  }
}
