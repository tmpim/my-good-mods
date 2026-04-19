package pw.tmpim.gooddeath.block

import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.nbt.NbtCompound
import net.minecraft.world.World

class TombstoneBlockEntity: BlockEntity() {
  var inventory: TombstoneInventory? = null

  val hasInventory: Boolean
    get() = this.inventory != null

  fun storePlayerInventory(inventory: PlayerInventory) {
    this.inventory = TombstoneInventory(inventory)
  }

  fun dropInventory(world: World, x: Int, y: Int, z: Int) {
    inventory?.let {
      for (slot in 0 until it.size()) {
        it.dropStack(world, x, y, z, slot)
      }
    }
  }

  override fun readNbt(nbt: NbtCompound) {
    super.readNbt(nbt)

    val hasInventory = nbt.getBoolean("HasInventory")

    if (hasInventory) {
      if (!nbt.contains("Inventory")) {
        throw IllegalStateException("Tombstone block entity NBT must have Inventory if HasInventory is true")
      }

      inventory = TombstoneInventory.readFromNbt(nbt.getList("Inventory"))
    }
  }

  override fun writeNbt(nbt: NbtCompound) {
    super.writeNbt(nbt)

    if (hasInventory) {
      nbt.putBoolean("HasInventory", true)

      val invList = inventory!!.writeNbt()
      nbt.put("Inventory", invList)
    } else {
      nbt.putBoolean("HasInventory", false)
    }
  }
}
