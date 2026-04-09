package pw.tmpim.goodstacks.mixin;

import net.minecraft.block.entity.FurnaceBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pw.tmpim.goodstacks.GoodStacks;

@Mixin(FurnaceBlockEntity.class)
public class FurnaceBlockEntityMixin {
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
