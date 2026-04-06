package pw.tmpim.goodcommandfixes

import net.glasslauncher.mods.gcapi3.api.ConfigRoot
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object GoodCommandFixes {
  const val MOD_ID = "good-command-fixes"
  const val MOD_NAME = "Good Command Fixes"

  @JvmField val log: Logger = LoggerFactory.getLogger(MOD_ID)

  @JvmStatic
  @ConfigRoot(value = MOD_ID, visibleName = MOD_NAME, nameKey = "$CONFIG_KEY.name")
  val config = GoodCommandFixesConfig()
}
