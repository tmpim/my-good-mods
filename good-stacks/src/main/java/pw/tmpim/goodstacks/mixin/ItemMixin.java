package pw.tmpim.goodstacks.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.item.Item;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import pw.tmpim.goodstacks.ItemExt;

@Mixin(Item.class)
public class ItemMixin implements ItemExt {
  @Mutable
  @Shadow
  protected int maxCount;

  @Unique
  private @Nullable Integer originalMaxCount = null;

  @Override
  public @Nullable Integer getGoodstacks$originalMaxCount() {
    return originalMaxCount;
  }

  @Override
  public void setGoodstacks$originalMaxCount(@Nullable Integer originalMaxCount) {
    this.originalMaxCount = originalMaxCount;
  }

  @Override
  public void goodstacks$setMaxCount(int maxCount) {
    this.maxCount = maxCount;
  }

  @WrapOperation(
    method = { "<init>", "setMaxCount" },
    at = @At(
      value = "FIELD",
      opcode = Opcodes.PUTFIELD,
      target = "Lnet/minecraft/item/Item;maxCount:I"
    )
  )
  public void trackOriginalMaxCount(Item item, int value, Operation<Void> original) {
    originalMaxCount = value;
    original.call(item, value);
  }
}
