package pw.tmpim.goodflags.recipe

import net.minecraft.inventory.CraftingInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.recipe.CraftingRecipe
import net.modificationstation.stationapi.api.item.StationItemNbt
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
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

  private fun ItemStack.isPainted(): Boolean {
    if (!this.isFlagItem()) return false
    val nbt = (this as? StationItemNbt)?.stationNbt ?: return false
    return nbt.contains("Pixels")
  }

  private fun ItemStack.pixelBytes(): ByteArray? {
    val nbt = (this as? StationItemNbt)?.stationNbt ?: return null
    return if (nbt.contains("Pixels")) nbt.getByteArray("Pixels") else null
  }

  override fun matches(inv: CraftingInventory): Boolean {
    var blankCount = 0
    var paintedCount = 0
    var otherCount = 0

    for (i in 0 until inv.size()) {
      val stack = inv.getStack(i) ?: continue
      if (stack.count <= 0) continue
      when {
        stack.isPainted()                        -> paintedCount++
        stack.isFlagItem() && !stack.isPainted() -> blankCount++
        else                                     -> otherCount++
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
        stack.isPainted()                        -> { paintedCount++; paintedStack = stack }
        stack.isFlagItem() && !stack.isPainted() -> blankCount++
      }
    }

    val source = paintedStack ?: return null

    return when {
      // Copy: return a new painted flag with the same pixels
      blankCount == 1 && paintedCount == 1 -> {
        val out = ItemStack(GoodFlags.flagBlock)
        val srcPixels = source.pixelBytes()
        if (srcPixels != null) out.stationNbt.putByteArray("Pixels", srcPixels)
        out
      }
      // Clear: return a blank flag
      blankCount == 0 && paintedCount == 1 -> ItemStack(GoodFlags.flagBlock)
      else -> null
    }
  }

  override fun getSize(): Int = 2

  override fun getOutput(): ItemStack = ItemStack(GoodFlags.flagBlock)

  fun handleTakeItem(input: Inventory, stack: ItemStack?, ci: CallbackInfo) {
    if (stack?.isFlagItem() != true) return
    if (input !is CraftingInventory) return
    if (!matches(input)) return

    // Determine if this is a Copy recipe (blank + painted) or Clear recipe (painted only).
    var blankCount = 0
    var paintedCount = 0
    var paintedSlot = -1

    for (i in 0..<input.size()) {
      val s: ItemStack? = input.getStack(i)
      if (s?.isFlagItem() != true) continue
      if (s.isPainted()) {
        paintedCount++
        paintedSlot = i
      } else {
        blankCount++
      }
    }

    if (blankCount == 1 && paintedCount == 1) {
      // Copy recipe: consume the blank flag, leave the painted flag in place.
      for (i in 0..<input.size()) {
        val s: ItemStack? = input.getStack(i)
        if (s?.isFlagItem() == true && !s.isPainted()) {
          input.removeStack(i, 1)
          break
        }
      }
      // Painted flag stays in its slot — do NOT remove it.
      ci.cancel()
    } else if (blankCount == 0 && paintedCount == 1) {
      // Clear recipe: consume the painted flag normally.
      if (paintedSlot >= 0) input.removeStack(paintedSlot, 1)
      ci.cancel()
    }
  }
}
