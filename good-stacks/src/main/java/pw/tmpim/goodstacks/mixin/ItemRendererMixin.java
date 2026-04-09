package pw.tmpim.goodstacks.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
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
    return ItemCount.formatItemCount(stack.count);
  }
}
