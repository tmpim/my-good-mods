package pw.tmpim.goodfood

import net.mine_diver.unsafeevents.listener.EventListener
import net.modificationstation.stationapi.api.event.mod.InitEvent
import pw.tmpim.goodfood.GoodFood.MOD_NAME
import pw.tmpim.goodfood.GoodFood.log

object GoodFoodClient {
  @EventListener
  fun onInit(event: InitEvent) {
    log.info("$MOD_NAME initializing client")
  }
}
