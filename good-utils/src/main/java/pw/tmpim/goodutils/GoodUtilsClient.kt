package pw.tmpim.goodutils

import net.fabricmc.api.ModInitializer
import net.mine_diver.unsafeevents.listener.EventListener
import net.modificationstation.stationapi.api.event.mod.InitEvent
import pw.tmpim.goodutils.GoodUtils.MOD_NAME
import pw.tmpim.goodutils.GoodUtils.log

object GoodUtilsClient : ModInitializer {
  override fun onInitialize() {}

  @EventListener
  fun onInit(event: InitEvent) {
    log.info("$MOD_NAME initializing client")
  }
}
