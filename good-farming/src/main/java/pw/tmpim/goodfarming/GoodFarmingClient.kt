package pw.tmpim.goodfarming

import net.fabricmc.api.ModInitializer
import net.mine_diver.unsafeevents.listener.EventListener
import net.modificationstation.stationapi.api.event.mod.InitEvent
import pw.tmpim.goodfarming.GoodFarming.MOD_NAME
import pw.tmpim.goodfarming.GoodFarming.log

object GoodFarmingClient : ModInitializer {
  override fun onInitialize() {}

  @EventListener
  fun onInit(event: InitEvent) {
    log.info("$MOD_NAME initializing client")
  }
}
