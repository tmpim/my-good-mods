package pw.tmpim.goodmodtemplate

import net.mine_diver.unsafeevents.listener.EventListener
import net.modificationstation.stationapi.api.event.mod.InitEvent
import pw.tmpim.goodmodtemplate.GoodModTemplate.MOD_NAME
import pw.tmpim.goodmodtemplate.GoodModTemplate.log

object GoodModTemplateClient {
  @EventListener
  fun onInit(event: InitEvent) {
    log.info("$MOD_NAME initializing client")
  }
}
