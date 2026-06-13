package pw.tmpim.gooddeath.block

import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.world.World
import pw.tmpim.gooddeath.GoodDeath
import pw.tmpim.goodutils.nbt.NbtType
import pw.tmpim.goodutils.nbt.getType

class TombstoneBlockEntity: BlockEntity() {
  private var inventory: TombstoneInventory? = null
  private var owner: String? = null

  val hasInventory: Boolean
    get() = this.inventory != null

  val hasOwner: Boolean
    get() = this.owner != null

  fun bury(playerEntity: PlayerEntity) {
    this.inventory = TombstoneInventory(playerEntity.inventory)
    this.owner = playerEntity.name
  }

  fun dropInventory(world: World, x: Int, y: Int, z: Int): Boolean {
    inventory?.let {
      for (slot in 0 until it.size()) {
        it.dropStack(world, x, y, z, slot)
      }

      return true
    }

    return false
  }

  override fun readNbt(nbt: NbtCompound) {
    super.readNbt(nbt)

    if (nbt.contains("Inventory")) {
      inventory = TombstoneInventory.readFromNbt(nbt.getList("Inventory"))
    }

    if (nbt.contains("Owner")) {
      owner = nbt.getString("Owner")
    }
  }

  override fun writeNbt(nbt: NbtCompound) {
    super.writeNbt(nbt)

    inventory?.let { nbt.put("Inventory", it.writeNbt()) }
    owner?.let { nbt.putString("Owner", it) }
  }
}
