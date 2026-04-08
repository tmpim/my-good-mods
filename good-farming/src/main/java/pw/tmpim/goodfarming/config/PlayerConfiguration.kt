package pw.tmpim.goodfarming.config

import net.minecraft.nbt.NbtCompound

data class PlayerConfiguration(
  var tramplingNerfEnabled      : Boolean = true,
  var bonemealWastageFixEnabled : Boolean = true,
  var quickReplantingEnabled    : Boolean = true,
  var seedBagAutoPickupEnabled  : Boolean = true,
) {
  fun toNbt(nbt: NbtCompound) {
    nbt.putBoolean("tramplingNerfEnabled",      tramplingNerfEnabled)
    nbt.putBoolean("bonemealWastageFixEnabled", bonemealWastageFixEnabled)
    nbt.putBoolean("quickReplantingEnabled",    quickReplantingEnabled)
    nbt.putBoolean("seedBagAutoPickupEnabled",  seedBagAutoPickupEnabled)
  }

  companion object {
    fun fromNbt(nbt: NbtCompound) = PlayerConfiguration(
      tramplingNerfEnabled      = nbt.getBoolean("tramplingNerfEnabled"),
      bonemealWastageFixEnabled = nbt.getBoolean("bonemealWastageFixEnabled"),
      quickReplantingEnabled    = nbt.getBoolean("quickReplantingEnabled"),
      seedBagAutoPickupEnabled  = nbt.getBoolean("seedBagAutoPickupEnabled"),
    )
  }
}
