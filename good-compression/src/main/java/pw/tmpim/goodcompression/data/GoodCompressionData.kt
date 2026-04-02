package pw.tmpim.goodcompression.data

import emmathemartian.datagen.DataGenContext
import emmathemartian.datagen.entrypoint.DataEntrypoint
import net.modificationstation.stationapi.api.util.Namespace

object GoodCompressionData : DataEntrypoint {
  val namespace: Namespace = Namespace.resolve()

  override fun run() {
    val ctx = DataGenContext(namespace)

    ctx.run(GoodCompressionBlockModelProvider(ctx))
    ctx.run(GoodCompressionBlockStateProvider(ctx))
    ctx.run(GoodCompressionBlockTagProvider(ctx))
    ctx.run(GoodCompressionCraftingRecipeProvider(ctx))
    ctx.run(GoodCompressionLanguageProvider(ctx))
    ctx.run(GoodCompressionItemModelProvider(ctx))
    ctx.run(MinecraftTagProvider(DataGenContext(Namespace.MINECRAFT)))
  }
}
