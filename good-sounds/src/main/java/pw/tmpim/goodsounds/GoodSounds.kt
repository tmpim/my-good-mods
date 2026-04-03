package pw.tmpim.goodsounds

import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.glasslauncher.mods.gcapi3.api.ConfigRoot
import net.mine_diver.unsafeevents.listener.EventListener
import net.modificationstation.stationapi.api.event.mod.InitEvent
import net.modificationstation.stationapi.api.util.Namespace
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object GoodSounds : ModInitializer {
  const val MOD_ID = "good-sounds"
  const val MOD_NAME = "Good Sounds"
  val MOD_VERSION = FabricLoader.getInstance().getModContainer(MOD_ID).get().metadata.version.toString()

  val namespace: Namespace = Namespace.resolve()

  @JvmField val log: Logger = LoggerFactory.getLogger(MOD_ID)

  @JvmStatic
  @ConfigRoot(value = MOD_ID, visibleName = MOD_NAME, nameKey = "$CONFIG_KEY.name")
  val config = GoodModConfig()

  override fun onInitialize() {}

  @EventListener
  fun onInit(event: InitEvent) {
    log.info("$MOD_NAME initializing")
  }
}
