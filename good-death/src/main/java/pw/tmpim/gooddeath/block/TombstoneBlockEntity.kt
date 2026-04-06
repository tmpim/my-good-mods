package pw.tmpim.gooddeath.block

import net.minecraft.block.entity.BlockEntity
import net.minecraft.nbt.NbtCompound

class TombstoneBlockEntity(owner: String?): BlockEntity() {
  val hasOwner: Boolean
    get() = this.owner != null

  var owner: String? = owner
    set(newOwner) {
      if (newOwner != null && newOwner.isBlank()) {
        throw IllegalArgumentException("Tombstone owner must not be a blank string. Use null for no owner")
      }

      field = newOwner
    }

  override fun readNbt(nbt: NbtCompound?) {
    super.readNbt(nbt)

    if (nbt != null) {
      val hasOwner = nbt.getBoolean("HasOwner")

      if (hasOwner) {
        if (nbt.contains("Owner")) {
          throw IllegalStateException("Tombstone block entity NBT must have Owner if HasOwner is true")
        } else {
          owner = nbt.getString("Owner")
        }
      }
    }
  }

  override fun writeNbt(nbt: NbtCompound?) {
    super.writeNbt(nbt)

    if (hasOwner) {
      nbt?.putBoolean("HasOwner", true)
      nbt?.putString("Owner", owner)
    } else {
      nbt?.putBoolean("HasOwner", false)
    }
  }
}
