package pw.tmpim.goodflags.net

import net.minecraft.entity.player.PlayerEntity
import net.modificationstation.stationapi.api.network.packet.MessagePacket
import net.modificationstation.stationapi.api.network.packet.PacketHelper
import pw.tmpim.goodflags.GoodFlags
import pw.tmpim.goodflags.block.FlagBlockEntity
import pw.tmpim.goodflags.block.FlagSpec.FLAG_HEIGHT
import pw.tmpim.goodflags.block.FlagSpec.FLAG_WIDTH

object FlagNetworking {
  /** Client -> Server: player edited a flag */
  val FLAG_UPDATE_ID = GoodFlags.namespace.id("flag_update")
  /** Server -> Client: sync flag pixel data on chunk load */
  val FLAG_SYNC_ID = GoodFlags.namespace.id("flag_sync")

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
   * Send flag pixel data from the client to the server.
   */
  fun sendFlagUpdate(x: Int, y: Int, z: Int, pixels: ByteArray) {
    val packet = MessagePacket(FLAG_UPDATE_ID)
    packet.ints = intArrayOf(x, y, z)
    packet.bytes = pixels.copyOf()
    packet.worldPacket = true
    PacketHelper.send(packet)
  }

  /**
   * Handle a flag update packet (received on the server from a client edit).
   */
  fun handleFlagUpdate(player: PlayerEntity, packet: MessagePacket) {
    val ints = packet.ints ?: return
    val bytes = packet.bytes ?: return

    if (ints.size < 3) return
    if (bytes.size != FLAG_WIDTH * FLAG_HEIGHT) return

    val x = ints[0]
    val y = ints[1]
    val z = ints[2]

    // Validate distance (player must be within 8 blocks)
    val dx = player.x - (x + 0.5)
    val dy = player.y - (y + 0.5)
    val dz = player.z - (z + 0.5)
    if (dx * dx + dy * dy + dz * dz > 64.0) {
      GoodFlags.log.warn("Player ${player.name} tried to edit a flag too far away")
      return
    }

    val world = player.world ?: return
    val entity = world.getBlockEntity(x, y, z)
    if (entity !is FlagBlockEntity) {
      GoodFlags.log.warn("Player ${player.name} tried to edit a non-flag block entity at $x, $y, $z")
      return
    }

    // Validate all bytes are valid color indices
    for (b in bytes) {
      if (b.toInt() and 0xFF > 15) return
    }

    entity.setAllPixels(bytes)
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
