package pw.tmpim.gooddeath.block

import net.minecraft.entity.ItemEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList
import net.minecraft.world.World

class TombstoneInventory(val items: Array<ItemStack?>) : Inventory {
  var dirty = false

  constructor(playerInventory: PlayerInventory) : this(
    TombstoneInventory.run {
      // Sequence<T> does not have toTypedArray() so this is the only way to do it in constant space.
      val itemList = arrayListOf<ItemStack?>()

      for (item in playerInventory.main) {
        if (item != null) {
          itemList.add(item)
        }
      }

      for (item in playerInventory.armor) {
        if (item != null) {
          itemList.add(item)
        }
      }

      itemList.toTypedArray()
    })

  fun dropStack(world: World, x: Int, y: Int, z: Int, slot: Int) {
    if (!items.indices.contains(slot)) {
      throw IllegalArgumentException("slot $slot is not in range of inventory with size ${items.size}")
    }

    val stack = getStack(slot)
    if (stack != null) {
      val rand = world.random

      // vanilla chest behaviour
      val spawnX = x + 0.5 + rand.nextDouble() * 0.8 + 0.1
      val spawnY = y + 0.5 + rand.nextDouble() * 0.8 + 0.1
      val spawnZ = z + 0.5 + rand.nextDouble() * 0.8 + 0.1

      val itemEntity = ItemEntity(world, spawnX, spawnY, spawnZ, stack)
      // also vanilla chest behavior
      itemEntity.velocityX = rand.nextGaussian() * 0.05
      itemEntity.velocityY = rand.nextGaussian() * 0.05 + 0.2
      itemEntity.velocityZ = rand.nextGaussian() * 0.05

      world.spawnEntity(itemEntity)
    }
  }

  companion object {
    fun readFromNbt(nbt: NbtList): TombstoneInventory {
      val items = arrayListOf<ItemStack?>()

      for (i in 0 until nbt.size()) {
        val nbtItem = nbt.get(i)
        if (nbtItem is NbtCompound) {
          items.add(ItemStack(nbtItem))
        }
      }

      return TombstoneInventory(items.toTypedArray())
    }
  }

  fun writeNbt(): NbtList {
    val nbt = NbtList()

    items
      .asSequence()
      .filterNotNull()
      .map { stack ->
        val nbtItem = NbtCompound()
        stack.writeNbt(nbtItem)
        nbtItem
      }
      .forEach { nbtItem -> nbt.add(nbtItem) }

    return nbt
  }

  override fun size(): Int = items.size

  override fun getStack(slot: Int): ItemStack? = items.getOrNull(slot)

  override fun removeStack(slot: Int, amount: Int): ItemStack? {
    val stack = getStack(slot)

    if (stack != null) {
      if (stack.count <= amount) {
        setStack(slot, null)
      } else {
        val splitStack = stack.split(amount)

        if (splitStack.count == 0) {
          setStack(slot, null)
        }

        return splitStack
      }
    }

    return stack
  }

  override fun setStack(slot: Int, stack: ItemStack?) {
    items[slot] = stack
    markDirty()
  }

  override fun getName(): String = "Tombstone"

  override fun getMaxCountPerStack(): Int = 64

  override fun markDirty() {
    dirty = true
  }

  override fun canPlayerUse(player: PlayerEntity?): Boolean = false
}
