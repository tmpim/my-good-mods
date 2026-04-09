package pw.tmpim.goodstacks.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import pw.tmpim.goodstacks.GoodStacks;
import pw.tmpim.goodstacks.ItemCount;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
  @ModifyVariable(
    method = "renderGuiItemDecoration",
    at = @At("STORE")
  )
  public String formatItemCount(
    String val,
    @Local(argsOnly = true) ItemStack stack
  ) {
    if (Boolean.TRUE.equals(GoodStacks.getConfig().shortenItemCounts)) {
      return ItemCount.formatItemCount(stack.count);
    } else {
      return val;
    }
  }

  @Expression("?.count > 1")
  @Definition(id = "count", field = "Lnet/minecraft/item/ItemStack;count:I")
  @WrapOperation(
    method = "renderGuiItemDecoration",
    at = @At("MIXINEXTRAS:EXPRESSION")
  )
  public boolean showNegativeItemCount(
    int count,
    int one,
    Operation<Boolean> original
  ) {
    if (Boolean.TRUE.equals(GoodStacks.getConfig().negativeItemCounts)) {
      return count != 1;
    } else {
      return original.call(count, one);
    }
  }
}
