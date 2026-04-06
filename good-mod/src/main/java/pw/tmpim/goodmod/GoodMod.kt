package pw.tmpim.goodmod

import net.mine_diver.unsafeevents.listener.EventListener
import net.modificationstation.stationapi.api.event.mod.InitEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object GoodMod {
  const val MOD_ID = "good-mod"
  const val MOD_NAME = "My good mod"

  @JvmField val log: Logger = LoggerFactory.getLogger(MOD_ID)

  @EventListener
  fun onInit(event: InitEvent) {
    log.info("$MOD_NAME initializing")
  }
}
