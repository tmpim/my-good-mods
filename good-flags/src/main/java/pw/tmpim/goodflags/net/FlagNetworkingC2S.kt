package pw.tmpim.goodflags.net

import net.glasslauncher.mods.networking.GlassPacket
import net.minecraft.client.Minecraft
import net.minecraft.server.network.ServerPlayNetworkHandler
import pw.tmpim.goodflags.GoodFlags.log
import pw.tmpim.goodflags.GoodFlags.namespace
import pw.tmpim.goodflags.block.FlagBlockEntity
import pw.tmpim.goodflags.block.FlagSpec.FLAG_HEIGHT
import pw.tmpim.goodflags.block.FlagSpec.FLAG_WIDTH
import pw.tmpim.goodutils.net.GlassPacket

object FlagNetworkingC2S {
  /** Client -> Server: player edited a flag */
  val FLAG_UPDATE_ID = namespace.id("flag_update")

  /**
   * Send flag pixel data from the client to the server.
   */
  fun createFlagUpdatePacket(x: Int, y: Int, z: Int, pixels: ByteArray) =
    GlassPacket(FLAG_UPDATE_ID) {
      putInt("x", x)
      putInt("y", y)
      putInt("z", z)
      putByteArray("pixels", pixels.copyOf())
    }

  /**
   * Handle a flag update packet (received on the server from a client edit).
   */
  fun onFlagUpdate(packet: GlassPacket, handler: ServerPlayNetworkHandler?) {
    val player = handler?.player ?: Minecraft.INSTANCE.player
    val nbt = packet.nbt

    val x = nbt.getInt("x")
    val y = nbt.getInt("y")
    val z = nbt.getInt("z")
    val bytes = nbt.getByteArray("pixels")

    if (bytes.size != FLAG_WIDTH * FLAG_HEIGHT) return

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
