package pw.tmpim.goodflags.recipe

import net.minecraft.inventory.CraftingInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.recipe.CraftingRecipe
import net.modificationstation.stationapi.api.item.StationItemNbt
import net.modificationstation.stationapi.impl.item.StationNBTSetter
import pw.tmpim.goodflags.GoodFlags

/**
 * Two crafting recipes handled by a single class:
 *
 * 1. Copy:  blank flag + painted flag  →  painted flag copy
 *    (the source painted flag remains in the grid like a bucket)
 *
 * 2. Clear: painted flag alone          →  blank flag
 */
object FlagCraftingRecipe : CraftingRecipe {

  private fun ItemStack.isFlagItem() = itemId == GoodFlags.flagBlock.asItem().id

  /** Public so Java mixins can call it. */
  fun isPainted(stack: ItemStack): Boolean {
    if (!stack.isFlagItem()) return false
    val nbt = (stack as? StationItemNbt)?.stationNbt ?: return false
    return nbt.contains("Pixels")
  }

  private fun ItemStack.pixelNbt(): NbtCompound? {
    val nbt = (this as? StationItemNbt)?.stationNbt ?: return null
    return if (nbt.contains("Pixels")) nbt else null
  }

  override fun matches(inv: CraftingInventory): Boolean {
    var blankCount = 0
    var paintedCount = 0
    var otherCount = 0

    for (i in 0 until inv.size()) {
      val stack = inv.getStack(i) ?: continue
      if (stack.count <= 0) continue
      when {
        isPainted(stack)                            -> paintedCount++
        stack.isFlagItem() && !isPainted(stack)     -> blankCount++
        else                                        -> otherCount++
      }
    }

    if (otherCount > 0) return false

    // Copy: exactly one blank + exactly one painted
    if (blankCount == 1 && paintedCount == 1) return true

    // Clear: exactly one painted flag, nothing else
    if (blankCount == 0 && paintedCount == 1) return true

    return false
  }

  override fun craft(inv: CraftingInventory): ItemStack? {
    var blankCount = 0
    var paintedCount = 0
    var paintedStack: ItemStack? = null

    for (i in 0 until inv.size()) {
      val stack = inv.getStack(i) ?: continue
      if (stack.count <= 0) continue
      when {
        isPainted(stack)                        -> { paintedCount++; paintedStack = stack }
        stack.isFlagItem() && !isPainted(stack) -> blankCount++
      }
    }

    val source = paintedStack ?: return null

    return when {
      // Copy: return a new painted flag with the same pixels
      blankCount == 1 && paintedCount == 1 -> {
        val out = ItemStack(GoodFlags.flagBlock)
        val srcNbt = source.pixelNbt()
        if (srcNbt != null) StationNBTSetter.cast(out).setStationNbt(srcNbt.copy())
        out
      }
      // Clear: return a blank flag
      blankCount == 0 && paintedCount == 1 -> ItemStack(GoodFlags.flagBlock)
      else -> null
    }
  }

  override fun getSize(): Int = 2

  override fun getOutput(): ItemStack = ItemStack(GoodFlags.flagBlock)
}
