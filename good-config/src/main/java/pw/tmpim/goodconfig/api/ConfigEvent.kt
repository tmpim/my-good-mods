package pw.tmpim.goodconfig.api

import net.minecraft.entity.player.PlayerEntity

data class ConfigEvent<S : ConfigSpec>(
  val spec: S,
  val role: SpecRole,
  /** will be null for LOCAL and REMOTE, and populated for PLAYER SpecRole */
  val player: PlayerEntity? = null
)
