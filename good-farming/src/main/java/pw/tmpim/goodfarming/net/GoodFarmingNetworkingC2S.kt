package pw.tmpim.goodfarming.net

import net.glasslauncher.mods.networking.GlassPacket
import net.minecraft.client.Minecraft
import net.minecraft.server.network.ServerPlayNetworkHandler
import pw.tmpim.goodfarming.GoodFarming.log
import pw.tmpim.goodfarming.GoodFarming.namespace
import pw.tmpim.goodfarming.config.PlayerConfiguration
import pw.tmpim.goodfarming.config.PlayerConfigurationRegistry
import pw.tmpim.goodutils.net.GlassPacket
import pw.tmpim.goodutils.net.sendToServer

object GoodFarmingNetworkingC2S {
  /** Client -> Server: player configuration packet */
  val PLAYER_CONFIGURATION = namespace.id("player_configuration")

  /**
   * Sends the player's configuration to the server.
   */
  fun sendPlayerConfiguration() {
    GlassPacket(PLAYER_CONFIGURATION) {
      PlayerConfigurationRegistry.localConfig.toNbt(this)
    }.sendToServer()
  }

  /**
   * Handles the player's configuration sent to the server.
   */
  fun onPlayerConfiguration(packet: GlassPacket, handler: ServerPlayNetworkHandler?) {
    val player = handler?.player ?: Minecraft.INSTANCE.player
    try {
      PlayerConfigurationRegistry.putPlayerConfiguration(
        player,
        PlayerConfiguration.fromNbt(packet.nbt)
      )
    } catch (e: Exception) {
      log.error("invalid player configuration packet from ${player.name}", e)
    }
  }
}
