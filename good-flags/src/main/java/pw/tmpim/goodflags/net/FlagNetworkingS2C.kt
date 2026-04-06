package pw.tmpim.goodflags.net

import net.minecraft.entity.player.ClientPlayerEntity
import net.minecraft.entity.player.PlayerEntity
import net.modificationstation.stationapi.api.network.packet.MessagePacket
import pw.tmpim.goodflags.GoodFlags.namespace
import pw.tmpim.goodflags.block.FlagBlockEntity
import pw.tmpim.goodflags.block.FlagSpec.FLAG_HEIGHT
import pw.tmpim.goodflags.block.FlagSpec.FLAG_WIDTH
import pw.tmpim.goodflags.client.FlagPaintScreen

object FlagNetworkingS2C {
  /** Server -> Client: open flag GUI */
  val FLAG_SCREEN_OPEN_ID = namespace.id("flag_screen_open")
  /** Server -> Client: sync flag pixel data on chunk load */
  val FLAG_SYNC_ID = namespace.id("flag_sync")

  /**
   * Server -> client packet to request the client opens the flag screen for a given flag.
   * Called by FlagBlock.onUse().
   */
  fun createOpenPacket(x: Int, y: Int, z: Int): MessagePacket {
    val packet = MessagePacket(FLAG_SCREEN_OPEN_ID)
    packet.ints = intArrayOf(x, y, z)
    packet.worldPacket = true
    return packet
  }

  /**
   * Create a sync packet for server -> client block entity updates.
   * Called by FlagBlockEntity.createUpdatePacket().
   */
  fun createSyncPacket(x: Int, y: Int, z: Int, pixels: ByteArray): MessagePacket {
    val packet = MessagePacket(FLAG_SYNC_ID)
    packet.ints = intArrayOf(x, y, z)
    packet.bytes = pixels.copyOf()
    packet.worldPacket = true
    return packet
  }

  /**
   * Handle a flag screen open packet (received on the client when they right-click a flag).
   */
  fun handleFlagScreenOpen(player: PlayerEntity, packet: MessagePacket) {
    if (player !is ClientPlayerEntity) return

    val ints = packet.ints ?: return
    if (ints.size < 3) return

    val x = ints[0]
    val y = ints[1]
    val z = ints[2]

    val world = player.world ?: return
    val entity = world.getBlockEntity(x, y, z)
    if (entity !is FlagBlockEntity) return

    player.minecraft.setScreen(FlagPaintScreen(entity))
  }

  /**
   * Handle a flag sync packet (received on the client from the server on chunk load).
   */
  fun handleFlagSync(player: PlayerEntity, packet: MessagePacket) {
    val ints = packet.ints ?: return
    val bytes = packet.bytes ?: return

    if (ints.size < 3) return
    if (bytes.size != FLAG_WIDTH * FLAG_HEIGHT) return

    val x = ints[0]
    val y = ints[1]
    val z = ints[2]

    val world = player.world ?: return
    val entity = world.getBlockEntity(x, y, z)
    if (entity !is FlagBlockEntity) return

    entity.setAllPixels(bytes)
  }
}
