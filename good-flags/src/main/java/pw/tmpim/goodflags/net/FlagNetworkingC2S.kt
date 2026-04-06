package pw.tmpim.goodflags.net

import net.minecraft.entity.player.PlayerEntity
import net.modificationstation.stationapi.api.network.packet.MessagePacket
import net.modificationstation.stationapi.api.network.packet.PacketHelper
import pw.tmpim.goodflags.GoodFlags.log
import pw.tmpim.goodflags.GoodFlags.namespace
import pw.tmpim.goodflags.block.FlagBlockEntity
import pw.tmpim.goodflags.block.FlagSpec.FLAG_HEIGHT
import pw.tmpim.goodflags.block.FlagSpec.FLAG_WIDTH

object FlagNetworkingC2S {
  /** Client -> Server: player edited a flag */
  val FLAG_UPDATE_ID = namespace.id("flag_update")

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
      log.warn("Player ${player.name} tried to edit a flag too far away")
      return
    }

    val world = player.world ?: return
    val entity = world.getBlockEntity(x, y, z)
    if (entity !is FlagBlockEntity) {
      log.warn("Player ${player.name} tried to edit a non-flag block entity at $x, $y, $z")
      return
    }

    // Validate all bytes are valid color indices
    for (b in bytes) {
      if (b.toInt() and 0xFF > 15) return
    }

    // Update the underlying block entity
    entity.setAllPixels(bytes)
  }
}
