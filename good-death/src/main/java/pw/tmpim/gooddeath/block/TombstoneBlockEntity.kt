package pw.tmpim.gooddeath.block

import net.minecraft.block.entity.BlockEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList
import net.minecraft.world.World

class TombstoneBlockEntity(owner: String?): BlockEntity() {
  constructor() : this(null)

  val hasOwner: Boolean
    get() = this.owner != null

  var owner: String? = owner
    set(newOwner) {
      if (newOwner != null && newOwner.isBlank()) {
        throw IllegalArgumentException("Tombstone owner must not be a blank string. Use null for no owner")
      }

      field = newOwner
    }

  var inventory: TombstoneInventory? = null

  fun dropInventory(world: World, x: Int, y: Int, z: Int) {
    inventory?.let {
      for (slot in (0..it.size())) {
        it.dropStack(world, x, y, z, slot)
      }
    }
  }

  override fun readNbt(nbt: NbtCompound?) {
    super.readNbt(nbt)

    if (nbt != null) {
      val hasOwner = nbt.getBoolean("HasOwner")

      if (hasOwner) {
        if (!nbt.contains("Owner")) {
          throw IllegalStateException("Tombstone block entity NBT must have Owner if HasOwner is true")
        }

        owner = nbt.getString("Owner")
      }

      val hasInventory = nbt.getBoolean("HasInventory")

      if (hasInventory) {
        if (!nbt.contains("Inventory")) {
          throw IllegalStateException("Tombstone block entity NBT must have Inventory if HasInventory is true")
        }

        inventory = TombstoneInventory.readFromNbt(nbt.getList("Inventory"))
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

    if (inventory != null) {
      nbt?.putBoolean("HasInventory", true)

      val invList = inventory!!.writeNbt()
      nbt?.put("Inventory", invList)
    } else {
      nbt?.putBoolean("HasInventory", false)
    }
  }
}
