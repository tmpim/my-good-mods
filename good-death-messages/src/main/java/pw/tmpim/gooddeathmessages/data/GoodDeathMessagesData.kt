package pw.tmpim.gooddeathmessages.data

import emmathemartian.datagen.DataGenContext
import emmathemartian.datagen.entrypoint.DataEntrypoint
import net.modificationstation.stationapi.api.util.Namespace

object GoodDeathMessagesData : DataEntrypoint {
  val namespace: Namespace = Namespace.resolve()

  override fun run() {
    val ctx = DataGenContext(namespace)

    ctx.run(GoodDeathMessagesLanguageProvider(ctx))
  }
}
