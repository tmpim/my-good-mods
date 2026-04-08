package pw.tmpim.goodfarming.config

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.ServerPlayerEntity
import pw.tmpim.goodfarming.GoodFarming
import pw.tmpim.goodfarming.GoodFarming.log
import pw.tmpim.goodutils.misc.isServer
import java.util.*

object PlayerConfigurationRegistry {
  // TODO: ensure this is never accessed off the world thread
  private val playerConfigurationRegistry = WeakHashMap<ServerPlayerEntity, PlayerConfiguration>()

  val localConfig
    get() = with (GoodFarming.config) {
      PlayerConfiguration(
        tramplingNerfEnabled = tramplingNerfEnabled == true,
        bonemealWastageFixEnabled = bonemealWastageFixEnabled == true,
        quickReplantingEnabled = quickReplantingEnabled == true,
      )
    }

  /**
   * return's the player's configuration, or a server-default one if the player's configuration isn't available for
   * some reason
   */
  @JvmStatic
  fun getPlayerConfiguration(player: PlayerEntity): PlayerConfiguration =
    if (isServer) {
      playerConfigurationRegistry[player] ?: localConfig
    } else {
      localConfig
    }

  fun putPlayerConfiguration(player: ServerPlayerEntity, configuration: PlayerConfiguration) {
    log.debug("received configuration from {}", player.name)
    playerConfigurationRegistry[player] = configuration
  }
}
