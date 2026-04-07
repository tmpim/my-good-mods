package pw.tmpim.goodfarming.recipe

import net.minecraft.inventory.CraftingInventory
import net.minecraft.item.ItemStack
import net.minecraft.recipe.CraftingRecipe
import pw.tmpim.goodfarming.GoodFarming.seedBag
import pw.tmpim.goodfarming.item.SeedBagItem
import pw.tmpim.goodfarming.item.SeedTypeRegistry.getSeedType
import pw.tmpim.goodfarming.item.SeedTypeRegistry.isSeedValid
import kotlin.math.min

object SeedBagSeedsRecipe : CraftingRecipe {
  override fun matches(inv: CraftingInventory): Boolean {
    var hasSeedBag = false
    var hasSeeds = false
    var lastSeeds: ItemStack? = null

    for (i in 0 until inv.size()) {
      val stack = inv.getStack(i)
      if (stack == null || stack.count <= 0) continue

      when {
        stack.isOf(seedBag) -> {
          // only allow one seed bag to be added
          if (hasSeedBag) return false
          hasSeedBag = true
        }

        isSeedValid(stack) -> {
          // allow more than one stack of seeds to be added, as long as they're the same type
          if (lastSeeds != null && !lastSeeds.isItemEqual(stack)) {
            return false
          }

          hasSeeds = true
          lastSeeds = stack
        }

        else -> return false
      }
    }

    return hasSeedBag && hasSeeds
  }

  fun craft(inv: CraftingInventory, consume: Boolean): ItemStack? {
    // find the seed bag in the grid
    val (bagSlot, bagStack) = (0 until inv.size())
      .firstNotNullOfOrNull { i ->
        inv.getStack(i)
          ?.takeIf { it.count == 1 && it.isOf(seedBag) }
          ?.let { i to it }
      } ?: return null

    // get all the seed stacks. matches() should've already checked the homogeneity of the seeds
    val seedStacks = (0 until inv.size())
      .mapNotNull { i ->
        inv.getStack(i)
          ?.takeIf { it.count > 0 && isSeedValid(it) }
          ?.let { i to it }
      }

    val seedStackCount = seedStacks.sumOf { it.second.count }
    if (seedStackCount == 0) return null

    // refuse to craft if the seed bag's seed type is different
    val bagSeedsData = SeedBagItem.getStackSeeds(bagStack)
    if (bagSeedsData != null && seedStacks.any { !bagSeedsData.first.matches(it.second) }) {
      return null
    }

    // use the bag's existing seed type if present, otherwise figure out the new one
    val seedType = bagSeedsData?.first ?: getSeedType(seedStacks.first().second)

    // the remaining amount of seeds we can actually put in this bag
    var remainingSeedCount = min(seedStackCount, bagStack.maxDamage - (bagSeedsData?.second ?: 0))
    if (remainingSeedCount <= 0) return null // bag is full

    var finalSeedCount = bagSeedsData?.second ?: 0

    // try to consume as many seeds from each stack as possible
    for ((slot, seedStack) in seedStacks) {
      val amountToRemove = min(remainingSeedCount, seedStack.count)

      if (consume) inv.removeStack(slot, amountToRemove)
      remainingSeedCount -= amountToRemove
      finalSeedCount += amountToRemove

      if (remainingSeedCount <= 0) break
    }

    if (consume) inv.removeStack(bagSlot, bagStack.count)

    // output a new bag stack
    return bagStack.copy().also {
      SeedBagItem.setStackSeeds(it, seedType, finalSeedCount)
    }
  }

  // constructs the preview for the inventory. actual crafting operation is a mixin to CraftingResultSlot
  override fun craft(inv: CraftingInventory) = craft(inv, false)

  override fun getSize() = 2

  override fun getOutput(): ItemStack =
    ItemStack(seedBag)
}
