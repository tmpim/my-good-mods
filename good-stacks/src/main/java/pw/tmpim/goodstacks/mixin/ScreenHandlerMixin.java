package pw.tmpim.goodstacks.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.screen.ScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ScreenHandler.class)
public class ScreenHandlerMixin {
  @Definition(id = "count", field = "Lnet/minecraft/item/ItemStack;count:I")
  @Expression("(?.count + 1) / 2")
  @ModifyExpressionValue(
    method = "onSlotClick",
    at = @At("MIXINEXTRAS:EXPRESSION")
  )
  public int preventStackSplitOverflow(int original) {
    return Math.abs(original);
  }

  @Definition(id = "getMaxCount", method = "Lnet/minecraft/item/ItemStack;getMaxCount()I")
  @Expression("? <= ?.getMaxCount()")
  @ModifyExpressionValue(
    method = "insertItem",
    at = @At("MIXINEXTRAS:EXPRESSION")
  )
  public boolean preventShiftClickOverflow(
    boolean original,
    @Local(ordinal = 3) int var8
  ) {
    return var8 > 0 && original;
  }
}
