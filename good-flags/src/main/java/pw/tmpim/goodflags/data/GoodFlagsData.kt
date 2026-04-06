package pw.tmpim.goodflags.data

import emmathemartian.datagen.DataGenContext
import emmathemartian.datagen.entrypoint.DataEntrypoint
import net.modificationstation.stationapi.api.util.Namespace

object GoodFlagsData : DataEntrypoint {
  val namespace: Namespace = Namespace.resolve()

  override fun run() {
    val ctx = DataGenContext(namespace)
    ctx.run(GoodFlagsCraftingRecipeProvider(ctx))
    ctx.run(GoodFlagsLanguageProvider(ctx))
  }
}
