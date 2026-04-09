package pw.tmpim.goodstacks.mixin.ami;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.glasslauncher.mods.alwaysmoreitems.config.AMIConfig;
import net.glasslauncher.mods.alwaysmoreitems.gui.screen.OverlayScreen;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import static pw.tmpim.goodstacks.ItemExtKt.nbtCountKey;

@Mixin(value = OverlayScreen.class)
public class AmiOverlayScreenMixin {
  @Shadow
  public OverlayScreen.ItemRenderEntry hoveredItem;

  @Definition(id = "putByte", method = "Lnet/minecraft/nbt/NbtCompound;putByte(Ljava/lang/String;B)V")
  @Definition(id = "nbt", local = @Local(type = NbtCompound.class))
  @Expression("nbt.putByte('Count', (byte) ?)")
  @WrapOperation(method = "mouseClicked", at = @At("MIXINEXTRAS:EXPRESSION"))
  public void writeCustomCount(
    NbtCompound nbt,
    String key,
    byte b,
    Operation<Void> original,
    @Local(argsOnly = true, name = "button") int button
  ) {
    // keep the original call in case the server doesn't have our mixin, but cap it to 64
    var maxCount = hoveredItem.item().getMaxCount();
    if (button == 0 && maxCount > 64) {
      nbt.putInt(nbtCountKey, maxCount); // the itemstack deserialiser should understand it already
      original.call(nbt, key, (byte) 64);
    } else if (button == 1 && AMIConfig.getRightClickGiveAmount() > 64) {
      nbt.putInt(nbtCountKey, Math.min(AMIConfig.getRightClickGiveAmount(), maxCount));
      original.call(nbt, key, (byte) 64);
    }
  }
}
