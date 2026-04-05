package pw.tmpim.goodclumps

import net.fabricmc.api.ModInitializer
import net.glasslauncher.mods.gcapi3.api.ConfigRoot
import net.mine_diver.unsafeevents.listener.EventListener
import net.modificationstation.stationapi.api.event.mod.InitEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object GoodClumps : ModInitializer {
  const val MOD_ID = "good-clumps"
  const val MOD_NAME = "Good Clumps"

  @JvmField val log: Logger = LoggerFactory.getLogger(MOD_ID)

  @JvmStatic
  @ConfigRoot(value = MOD_ID, visibleName = MOD_NAME, nameKey = "$CONFIG_KEY.name")
  val config = GoodClumpsConfig()

  override fun onInitialize() {}

  @EventListener
  fun onInit(event: InitEvent) {
    log.info("$MOD_NAME initializing")
  }

  @JvmStatic
  val enabled: Boolean
    get() = config.itemMergeEnabled == true
}
