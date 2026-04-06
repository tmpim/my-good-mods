package pw.tmpim.gooddeath

import net.fabricmc.api.ModInitializer
import net.mine_diver.unsafeevents.listener.EventListener
import net.modificationstation.stationapi.api.event.mod.InitEvent
import pw.tmpim.gooddeath.GoodDeath.MOD_NAME
import pw.tmpim.gooddeath.GoodDeath.log

object GoodDeathClient : ModInitializer {
  override fun onInitialize() {}

  @EventListener
  fun onInit(event: InitEvent) {
    log.info("$MOD_NAME initializing client")
  }
}
