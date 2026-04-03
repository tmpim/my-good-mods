package pw.tmpim.goodsounds

import net.fabricmc.api.ModInitializer
import net.mine_diver.unsafeevents.listener.EventListener
import net.modificationstation.stationapi.api.event.mod.InitEvent
import pw.tmpim.goodsounds.GoodSounds.MOD_NAME
import pw.tmpim.goodsounds.GoodSounds.log

object GoodSoundsClient : ModInitializer {
  override fun onInitialize() {}

  @EventListener
  fun onInit(event: InitEvent) {
    log.info("$MOD_NAME initializing client")
  }
}
