package pw.tmpim.goodassetfetcher

import net.fabricmc.loader.api.FabricLoader
import net.mine_diver.unsafeevents.listener.EventListener
import net.modificationstation.stationapi.api.StationAPI
import net.modificationstation.stationapi.api.event.mod.InitEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import pw.tmpim.goodassetfetcher.api.GoodAssetFetcherRegistryEvent

object GoodAssetFetcher {
  const val MOD_ID = "good-asset-fetcher"
  const val MOD_NAME = "Good Asset Fetcher"
  val MOD_VERSION = FabricLoader.getInstance().getModContainer(MOD_ID).get().metadata.version.toString()

  const val PACK_NAME = "$MOD_NAME Generated"

  @JvmField val log: Logger = LoggerFactory.getLogger(MOD_ID)

  @EventListener
  fun onInit(event: InitEvent) {
    log.info("$MOD_NAME initializing client")

    try {
      AssetFetcherImpl.init()
    } catch (e: Exception) {
      log.error("could not load or fetch version manifest. assets will not be loaded", e)
      return
    }

    // fetch the register events from other mods
    try {
      StationAPI.EVENT_BUS.post(GoodAssetFetcherRegistryEvent(GoodAssetFetcherRegistryImpl))
    } catch (e: Exception) {
      log.error("error while invoking GoodAssetFetcherRegistryEvent. some assets may not be loaded", e)
    }
  }
}
