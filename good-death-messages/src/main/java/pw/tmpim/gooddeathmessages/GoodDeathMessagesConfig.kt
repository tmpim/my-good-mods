package pw.tmpim.gooddeathmessages

import net.glasslauncher.mods.gcapi3.api.ConfigEntry
import pw.tmpim.gooddeathmessages.GoodDeathMessages.MOD_ID

internal const val CONFIG_KEY = "gui.${MOD_ID}.config"
private const val C = CONFIG_KEY

class GoodDeathMessagesConfig {
  @JvmField
  @ConfigEntry(
    name = "Enable death messages",
    nameKey = "$C.death_messages_enabled",
  )
  var deathMessagesEnabled: Boolean? = true

  @JvmField
  @ConfigEntry(
    name = "Enable non-vanilla death messages",
    nameKey = "$C.custom_death_messages_enabled",
    description = "Enables Good Death Messages' own additional death messages. Does not affect any other mods adding " +
      "their own death messages.",
    descriptionKey = "$C.custom_death_messages_enabled.desc",
  )
  var customDeathMessagesEnabled: Boolean? = true
}
