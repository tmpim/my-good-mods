package pw.tmpim.goodstacks.mixin;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pw.tmpim.goodstacks.GoodStacks;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {
  @Inject(
    method = "getMaxCountPerStack",
    at = @At("RETURN"),
    cancellable = true
  )
  public void modifyMaxCountPerStack(CallbackInfoReturnable<Integer> cir) {
    if (cir.getReturnValueI() != 64) return;
    GoodStacks.getMaxStack().ifPresent(cir::setReturnValue);
  }
}
