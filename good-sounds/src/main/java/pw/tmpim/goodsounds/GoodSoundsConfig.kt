package pw.tmpim.goodsounds

import net.glasslauncher.mods.gcapi3.api.ConfigEntry
import pw.tmpim.goodsounds.GoodSounds.MOD_ID

internal const val CONFIG_KEY = "gui.${MOD_ID}.config"
private const val C = CONFIG_KEY

class GoodSoundsConfig {
  @JvmField
  @ConfigEntry(
    name = "Rain volume",
    nameKey = "$C.rain_volume",
    description = "How loud the rain is, as a fraction of the vanilla volume (0.00-1.00).",
    descriptionKey = "$C.rain_volume.desc",
    minValue = 0.0,
    maxValue = 1.0
  )
  var rainVolume: Float? = GoodSounds.DEFAULT_RAIN_VOLUME

  @JvmField
  @ConfigEntry(
    name = "Metal pipe on hit",
    nameKey = "$C.metal_pipe_on_hit_enabled",
    description = "Plays the metal pipe sound when hitting entities with an iron item.",
    descriptionKey = "$C.metal_pipe_on_hit_enabled.desc",
  )
  var metalPipeOnHit: Boolean? = false

  @JvmField
  @ConfigEntry(
    name = "Metal pipe on fall damage",
    nameKey = "$C.metal_pipe_on_fall_enabled",
    description = "Plays the metal pipe when taking fall damage with iron boots.",
    descriptionKey = "$C.metal_pipe_on_fall_enabled.desc",
  )
  var metalPipeOnFall: Boolean? = false

  @JvmField
  @ConfigEntry(
    name = "Metal pipe volume",
    nameKey = "$C.metal_pipe_volume",
    description = "Volume of the metal pipe sound (0.00-1.00).",
    descriptionKey = "$C.metal_pipe_volume.desc",
    minValue = 0.0,
    maxValue = 1.0
  )
  var metalPipeVolume: Float? = 1.0f
}
