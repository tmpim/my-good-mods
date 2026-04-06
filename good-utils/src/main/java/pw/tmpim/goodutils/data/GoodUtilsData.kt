package pw.tmpim.goodutils.data

import emmathemartian.datagen.DataGenContext
import emmathemartian.datagen.entrypoint.DataEntrypoint
import net.modificationstation.stationapi.api.util.Namespace

object GoodUtilsData : DataEntrypoint {
  val namespace: Namespace = Namespace.resolve()

  override fun run() {
    val ctx = DataGenContext(namespace)
    ctx.run(GoodUtilsLanguageProvider(ctx))
  }
}
