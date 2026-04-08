package pw.tmpim.goodfarming.item

import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import pw.tmpim.goodfarming.GoodFarming.seedBag
import pw.tmpim.goodfarming.item.SeedBagItem.Companion.getStackSeeds
import pw.tmpim.goodfarming.item.SeedBagItem.Companion.setStackSeeds
import kotlin.math.min

object SeedPickup {
  /**
   * attempt to place picked up seeds in the player's Seed Bags. return false to delegate back to inventory insertion,
   * or true to cancel the pickup
   */
  @JvmStatic
  fun onItemPickup(inv: PlayerInventory, seedStack: ItemStack): Boolean {
    if (seedStack.count <= 0) return false

    // test the selected slot first
    inv.selectedItem?.let { insertIntoSeedBag(it, seedStack) }

    // test the rest of the inventory
    for (it in inv.main) {
      if (it == null || it.count <= 0) continue
      if (seedStack.count <= 0) break
      insertIntoSeedBag(it, seedStack)
    }

    // cancel the pickup if we consumed all the seeds, otherwise delegate back to inventory insertion
    return seedStack.count <= 0
  }

  private fun insertIntoSeedBag(bagStack: ItemStack, seedStack: ItemStack) {
    if (seedStack.count <= 0 || !bagStack.isOf(seedBag)) {
      return
    }

    val (seedType, count) = getStackSeeds(bagStack) ?: return
    if (
      // ensure the bag isn't empty, and also that it isn't full:
      count !in 1 until bagStack.maxDamage
      // ensure the seed belongs in the bag
      || !seedType.matches(seedStack)
    ) {
      return
    }

    val toConsume = min(seedStack.count, bagStack.maxDamage - count)
    seedStack.count -= toConsume
    setStackSeeds(bagStack, seedType, count + toConsume)
  }
}
