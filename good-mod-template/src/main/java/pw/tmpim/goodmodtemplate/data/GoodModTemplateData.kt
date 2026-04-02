package pw.tmpim.goodmodtemplate.data

import emmathemartian.datagen.DataGenContext
import emmathemartian.datagen.entrypoint.DataEntrypoint
import net.modificationstation.stationapi.api.util.Namespace

object GoodModTemplateData : DataEntrypoint {
  val namespace: Namespace = Namespace.resolve()

  override fun run() {
    val ctx = DataGenContext(namespace)

    ctx.run(GoodModTemplateBlockModelProvider(ctx))
    ctx.run(GoodModTemplateBlockStateProvider(ctx))
    ctx.run(GoodModTemplateCraftingRecipeProvider(ctx))
    ctx.run(GoodModTemplateItemModelProvider(ctx))
    ctx.run(GoodModTemplateLanguageProvider(ctx))
    ctx.run(GoodModTemplateBlockTagProvider(ctx))
    ctx.run(GoodModTemplateItemTagProvider(ctx))
  }
}
