package pw.tmpim.goodconfig

import net.mine_diver.unsafeevents.listener.EventListener
import net.modificationstation.stationapi.api.event.mod.InitEvent
import pw.tmpim.goodconfig.GoodConfig.MOD_NAME
import pw.tmpim.goodconfig.GoodConfig.log

object GoodConfigClient {
  @EventListener
  fun onInit(event: InitEvent) {
    log.info("$MOD_NAME initializing client")
  }
}
