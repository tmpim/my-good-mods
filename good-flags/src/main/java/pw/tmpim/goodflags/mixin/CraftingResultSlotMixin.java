package pw.tmpim.goodflags.mixin;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.CraftingResultSlot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pw.tmpim.goodflags.GoodFlags;
import pw.tmpim.goodflags.recipe.FlagCraftingRecipe;

@Mixin(CraftingResultSlot.class)
public class CraftingResultSlotMixin {
  @Shadow
  @Final
  private Inventory input;

  private static boolean isFlagStack(ItemStack s) {
    return s != null && s.count > 0 && s.itemId == GoodFlags.flagBlock.asItem().id;
  }

  @Inject(
    method = "onTakeItem",
    at = @At("HEAD"),
    cancellable = true
  )
  public void handleFlagRecipe(ItemStack stack, CallbackInfo ci) {
    if (!isFlagStack(stack)) return;
    if (!(input instanceof CraftingInventory inv)) return;
    if (!FlagCraftingRecipe.INSTANCE.matches(inv)) return;

    // Determine if this is a Copy recipe (blank + painted) or Clear recipe (painted only).
    int blankCount = 0;
    int paintedCount = 0;
    int paintedSlot = -1;

    for (int i = 0; i < inv.size(); i++) {
      ItemStack s = inv.getStack(i);
      if (!isFlagStack(s)) continue;
      if (FlagCraftingRecipe.INSTANCE.isPainted(s)) {
        paintedCount++;
        paintedSlot = i;
      } else {
        blankCount++;
      }
    }

    if (blankCount == 1 && paintedCount == 1) {
      // Copy recipe: consume the blank flag, leave the painted flag in place.
      for (int i = 0; i < inv.size(); i++) {
        ItemStack s = inv.getStack(i);
        if (isFlagStack(s) && !FlagCraftingRecipe.INSTANCE.isPainted(s)) {
          inv.removeStack(i, 1);
          break;
        }
      }
      // Painted flag stays in its slot — do NOT remove it.
      ci.cancel();
    } else if (blankCount == 0 && paintedCount == 1) {
      // Clear recipe: consume the painted flag normally.
      if (paintedSlot >= 0) inv.removeStack(paintedSlot, 1);
      ci.cancel();
    }
  }
}
