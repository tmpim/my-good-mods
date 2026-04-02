package pw.tmpim.goodcommandfixes

import net.glasslauncher.mods.gcapi3.api.ConfigEntry
import pw.tmpim.goodcommandfixes.GoodCommandFixes.MOD_ID

internal const val CONFIG_KEY = "gui.${MOD_ID}.config"
private const val C = CONFIG_KEY

class GoodCommandFixesConfig {
  @JvmField
  @ConfigEntry(
    name = "Allow all players to run /list",
    nameKey = "$C.global_list_enabled"
  )
  var globalListEnabled: Boolean? = true

  @JvmField
  @ConfigEntry(
    name = "Show /tell feedback to senders",
    nameKey = "$C.tell_feedback_enabled"
  )
  var tellFeedbackEnabled: Boolean? = true
}
