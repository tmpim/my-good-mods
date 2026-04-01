package pw.tmpim.goodmod.data

import emmathemartian.datagen.DataGenContext
import emmathemartian.datagen.entrypoint.DataEntrypoint
import net.modificationstation.stationapi.api.util.Namespace

object GoodModData : DataEntrypoint {
  val namespace: Namespace = Namespace.resolve()

  override fun run() {
    val ctx = DataGenContext(namespace)

    ctx.run(GoodBlockModelProvider(ctx))
    ctx.run(GoodBlockStateProvider(ctx))
    ctx.run(MinecraftTagProvider(DataGenContext(Namespace.MINECRAFT)))
    ctx.run(GoodCraftingRecipeProvider(ctx))
    ctx.run(GoodItemModelProvider(ctx))
    ctx.run(GoodLanguageProvider(ctx))
    ctx.run(GoodTagProvider(ctx))
  }
}
