package pw.tmpim.goodutils

import net.fabricmc.api.ModInitializer
import net.glasslauncher.mods.gcapi3.api.ConfigRoot
import net.mine_diver.unsafeevents.listener.EventListener
import net.modificationstation.stationapi.api.event.mod.InitEvent
import net.modificationstation.stationapi.api.util.Namespace
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object GoodUtils : ModInitializer {
  const val MOD_ID = "good-utils"
  const val MOD_NAME = "Good Utils"

  val namespace: Namespace = Namespace.resolve()

  @JvmField val log: Logger = LoggerFactory.getLogger(MOD_ID)

  @JvmStatic
  @ConfigRoot(value = MOD_ID, visibleName = MOD_NAME, nameKey = "$CONFIG_KEY.name")
  val config = GoodUtilsConfig()

  override fun onInitialize() {}

  @EventListener
  fun onInit(event: InitEvent) {
    log.info("$MOD_NAME initializing")
  }
}
