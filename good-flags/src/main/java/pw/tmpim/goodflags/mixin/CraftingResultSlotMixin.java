package pw.tmpim.goodflags.mixin;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.CraftingResultSlot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pw.tmpim.goodflags.recipe.FlagCraftingRecipe;

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
  public void handleTakeItem(ItemStack stack, CallbackInfo ci) {
    FlagCraftingRecipe.INSTANCE.handleTakeItem(input, stack, ci);
  }
}
