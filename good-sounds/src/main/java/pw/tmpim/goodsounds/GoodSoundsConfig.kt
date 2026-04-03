package pw.tmpim.goodsounds

import net.glasslauncher.mods.gcapi3.api.ConfigEntry
import pw.tmpim.goodsounds.GoodSounds.MOD_ID

internal const val CONFIG_KEY = "gui.${MOD_ID}.config"
private const val C = CONFIG_KEY

class GoodModConfig {
  @JvmField
  @ConfigEntry(
    name = "Metal pipe",
    nameKey = "$C.metal_pipe_enabled",
    description = "Plays the metal pipe sound when hitting entities with an iron item.",
    descriptionKey = "$C.metal_pipe_enabled.desc",
  )
  var metalPipe: Boolean? = true
}
