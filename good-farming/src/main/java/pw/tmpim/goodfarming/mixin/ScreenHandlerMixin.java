package pw.tmpim.goodfarming.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ScreenHandler.class)
public abstract class ScreenHandlerMixin {
  @Shadow
  public abstract void onSlotUpdate(Inventory inventory);

  /**
   * fixes a vanilla bug where adding an item to a slot doesn't call onSlotUpdate. this fixes the seed bag recipe
   * not updating when adding more than 1 seed to a slot (removing already works fine). this fix affects every screen
   * in the game, and is *technically* correct, but hopefully it doesn't have any real side effects....
   */
  @WrapOperation(
    method = "insertItem",
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/screen/slot/Slot;markDirty()V" // target all three markDirty() calls
    )
  )
  public void updateSlotsOnInsert(Slot instance, Operation<Void> original) {
    original.call(instance); // call markDirty() like normal

    // fix: call onSlotUpdate on the inventory, too
    var inv = instance.inventory;
    System.out.println("slot dirty " + instance + " inv: " + inv);
    if (inv != null) onSlotUpdate(inv);
  }

  /**
   * same as above
   */
  @Inject(
    method = "onSlotClick",
    at = @At(
      value = "FIELD",
      opcode = Opcodes.PUTFIELD,
      target = "Lnet/minecraft/item/ItemStack;count:I",
      ordinal = 0,
      shift = At.Shift.AFTER
    )
  )
  private void onStackAdd(
    CallbackInfoReturnable<ItemStack> cir,
    @Local(ordinal = 0) Slot slot
  ) {
    // fix: call onSlotUpdate on the inventory, too
    var inv = slot.inventory;
    System.out.println("slot dirty " + slot + " inv: " + inv);
    if (inv != null) onSlotUpdate(inv);
  }
}
