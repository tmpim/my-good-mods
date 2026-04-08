package pw.tmpim.goodflags.block

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.block.entity.BlockEntity
import net.minecraft.nbt.NbtCompound
import pw.tmpim.goodflags.block.FlagSpec.FLAG_HEIGHT
import pw.tmpim.goodflags.block.FlagSpec.FLAG_WIDTH
import pw.tmpim.goodflags.net.FlagNetworkingS2C

class FlagBlockEntity : BlockEntity() {
  /** Each byte is a color index 0-15. Row-major order: index = y * WIDTH + x */
  val pixels = ByteArray(FLAG_WIDTH * FLAG_HEIGHT).apply { fill(15) }

  /** Tracks whether the texture needs regenerating on the client. */
  @Transient
  var dirty = true

  fun getPixel(x: Int, y: Int): Int {
    if (x !in 0..<FLAG_WIDTH || y !in 0..<FLAG_HEIGHT) return 0
    return pixels[y * FLAG_WIDTH + x].toInt() and 0xFF
  }

  fun setAllPixels(data: ByteArray) {
    if (data.size != pixels.size) return
    System.arraycopy(data, 0, pixels, 0, pixels.size)
    dirty = true
    markDirty()
    world.blockUpdateEvent(x, y, z) // update the flag for other players
  }

  override fun writeNbt(nbt: NbtCompound) {
    super.writeNbt(nbt)
    nbt.putByteArray("Pixels", pixels)
  }

  override fun readNbt(nbt: NbtCompound) {
    super.readNbt(nbt)
    if (nbt.contains("Pixels")) {
      val data = nbt.getByteArray("Pixels")
      if (data.size == pixels.size) {
        System.arraycopy(data, 0, pixels, 0, pixels.size)
        dirty = true
      }
    }
  }

  /**
   * Called by the server when sending chunk data to clients.
   * Returns a Packet containing the flag's pixel data so the client receives it on login / chunk load.
   */
  @Environment(EnvType.SERVER)
  override fun createUpdatePacket() =
    FlagNetworkingS2C.createSyncPacket(x, y, z, pixels)
}
