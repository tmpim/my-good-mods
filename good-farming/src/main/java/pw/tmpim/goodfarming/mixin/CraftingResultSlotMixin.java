package pw.tmpim.goodfarming.mixin;

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
import pw.tmpim.goodfarming.GoodFarming;
import pw.tmpim.goodfarming.recipe.SeedBagSeedsRecipe;

@Mixin(CraftingResultSlot.class)
public class CraftingResultSlotMixin {
  @Shadow
  @Final
  private Inventory input;

  @Inject(
    method = "onTakeItem",
    at = @At("HEAD"),
    cancellable = true
  )
  public void checkSeedBagSeedsRecipe(ItemStack stack, CallbackInfo ci) {
    if (
      stack != null
      && stack.isOf(GoodFarming.seedBag)
      && input instanceof CraftingInventory inv
      // check the recipe again, because we don't know at this point if it's crafting a seed bag, or adding seeds to it
      && SeedBagSeedsRecipe.INSTANCE.matches(inv)
    ) {
      // the player is definitely taking the item, so it's safe to consume this time. craft again
      var out = SeedBagSeedsRecipe.INSTANCE.craft(inv, true);
      if (out != null) {
        // no need to do anything else. the cursor stack will be a different instance to our output, but they should
        // have the same effective result. the rest of this function is crafting callbacks, stats, and consuming the
        // items (which we should've done ourselves by now)
        ci.cancel();
      }
    }
  }
}
