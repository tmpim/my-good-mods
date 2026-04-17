package pw.tmpim.goodconfig

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import pw.tmpim.goodconfig.files.ConfigLoader.loadRegisteredConfigs
import pw.tmpim.goodconfig.files.ConfigWatcher

object GoodConfig : PreLaunchEntrypoint {
  const val MOD_ID = "good-config"
  const val MOD_NAME = "Good Config"

  @JvmField val log: Logger = LoggerFactory.getLogger(MOD_ID)

  override fun onPreLaunch() {
    log.info("$MOD_NAME initializing")
    loadRegisteredConfigs()
    ConfigWatcher.start()
  }

  @JvmStatic
  fun onStop() {
    ConfigWatcher.stop()
  }
}
