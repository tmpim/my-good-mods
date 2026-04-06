package pw.tmpim.goodutils

import net.mine_diver.unsafeevents.listener.EventListener
import net.modificationstation.stationapi.api.event.mod.InitEvent
import pw.tmpim.goodutils.GoodUtils.MOD_NAME
import pw.tmpim.goodutils.GoodUtils.log

object GoodUtilsClient {

  @EventListener
  fun onInit(event: InitEvent) {
    log.info("$MOD_NAME initializing client")
  }
}
