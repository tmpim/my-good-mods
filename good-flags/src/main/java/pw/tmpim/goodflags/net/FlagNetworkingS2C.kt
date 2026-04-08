package pw.tmpim.goodflags.net

import net.glasslauncher.mods.networking.GlassPacket
import net.minecraft.client.Minecraft
import net.minecraft.client.network.ClientNetworkHandler
import pw.tmpim.goodflags.GoodFlags.namespace
import pw.tmpim.goodflags.block.FlagBlockEntity
import pw.tmpim.goodflags.block.FlagSpec.FLAG_HEIGHT
import pw.tmpim.goodflags.block.FlagSpec.FLAG_WIDTH
import pw.tmpim.goodflags.client.FlagPaintScreen
import pw.tmpim.goodutils.net.GlassPacket


object FlagNetworkingS2C {
  /** Server -> Client: open flag GUI */
  val FLAG_SCREEN_OPEN_ID = namespace.id("flag_screen_open")
  /** Server -> Client: sync flag pixel data on chunk load */
  val FLAG_SYNC_ID = namespace.id("flag_sync")

  /**
   * Server -> client: request the client opens the flag screen for a given flag.
   * Called by FlagBlock.onUse().
   */
  fun createFlagScreenOpenPacket(x: Int, y: Int, z: Int) =
    GlassPacket(FLAG_SCREEN_OPEN_ID) {
      putInt("x", x)
      putInt("y", y)
      putInt("z", z)
    }

  /**
   * Create a sync packet for server -> client block entity updates.
   * Called by FlagBlockEntity.createUpdatePacket()
   */
  fun createSyncPacket(x: Int, y: Int, z: Int, pixels: ByteArray): GlassPacket =
    GlassPacket(FLAG_SYNC_ID) {
      putInt("x", x)
      putInt("y", y)
      putInt("z", z)
      putByteArray("pixels", pixels.copyOf())
    }

  /**
   * Handle a flag screen open packet (received on the client when they right-click a flag).
   */
  fun onFlagScreenOpen(packet: GlassPacket, handler: ClientNetworkHandler?) {
    val minecraft = handler?.minecraft ?: Minecraft.INSTANCE
    val world = handler?.world ?: minecraft.world
    val nbt = packet.nbt

    val x = nbt.getInt("x")
    val y = nbt.getInt("y")
    val z = nbt.getInt("z")

    val entity = world.getBlockEntity(x, y, z)
    if (entity !is FlagBlockEntity) return

    minecraft.setScreen(FlagPaintScreen(entity))
  }

  /**
   * Handle a flag sync packet (received on the client from the server on chunk load).
   */
  fun onFlagSync(packet: GlassPacket, handler: ClientNetworkHandler?) {
    val world = handler?.world ?: Minecraft.INSTANCE.world
    val nbt = packet.nbt

    val x = nbt.getInt("x")
    val y = nbt.getInt("y")
    val z = nbt.getInt("z")
    val bytes = nbt.getByteArray("pixels")

    if (bytes.size != FLAG_WIDTH * FLAG_HEIGHT) return

    val entity = world.getBlockEntity(x, y, z)
    if (entity !is FlagBlockEntity) return

    entity.setAllPixels(bytes)
  }
}
