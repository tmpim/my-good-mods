package pw.tmpim.goodcommandfixes.data

import emmathemartian.datagen.DataGenContext
import emmathemartian.datagen.entrypoint.DataEntrypoint
import net.modificationstation.stationapi.api.util.Namespace

object GoodCommandFixesData : DataEntrypoint {
  val namespace: Namespace = Namespace.resolve()

  override fun run() {
    val ctx = DataGenContext(namespace)
    ctx.run(GoodCommandFixesLanguageProvider(ctx))
  }
}
