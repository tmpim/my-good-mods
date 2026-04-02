package pw.tmpim.goodboatfix

import net.fabricmc.api.ModInitializer
import net.glasslauncher.mods.gcapi3.api.ConfigRoot
import net.mine_diver.unsafeevents.listener.EventListener
import net.modificationstation.stationapi.api.event.mod.InitEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object GoodBoatFix : ModInitializer {
  const val MOD_ID = "good-boat-fix"
  const val MOD_NAME = "Good Boat Fix"

  @JvmField val log: Logger = LoggerFactory.getLogger(MOD_ID)

  @JvmStatic
  @ConfigRoot(value = MOD_ID, visibleName = MOD_NAME, nameKey = "$CONFIG_KEY.name")
  val config = GoodBoatFixConfig()

  override fun onInitialize() {}

  @EventListener
  fun onInit(event: InitEvent) {
    log.info("$MOD_NAME initializing")

    // TODO: check for unitweaks conflict here
  }
}
