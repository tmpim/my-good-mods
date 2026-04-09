package pw.tmpim.goodstacks.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import pw.tmpim.goodutils.nbt.NbtExtKt;

import static pw.tmpim.goodstacks.ItemExtKt.nbtCountKey;

@Mixin(ItemStack.class)
public class ItemStackMixin {
  @Shadow
  public int count;

  @Definition(id = "count", field = "Lnet/minecraft/item/ItemStack;count:I")
  @Definition(id = "putByte", method = "Lnet/minecraft/nbt/NbtCompound;putByte(Ljava/lang/String;B)V")
  @Definition(id = "nbt", local = @Local(type = NbtCompound.class))
  @Expression("nbt.putByte('Count', (byte) this.count)")
  @WrapOperation(method = "writeNbt", at = @At("MIXINEXTRAS:EXPRESSION"))
  public void writeCustomCount(
    NbtCompound nbt,
    String key,
    byte b,
    Operation<Void> original
  ) {
    // do this even if max stacks have been disabled so we don't affect any existing world data (though we still risk
    // data loss if people uninstall the mod)
    // save the count capped to 64, and serialise the true count in our own key
    if (count > 64) {
      nbt.putInt(nbtCountKey, count);
      original.call(nbt, key, (byte) 64);
    } else {
      NbtExtKt.remove(nbt, nbtCountKey); // clean up our count key as a space optimisation

      // prevent ever writing a negative count; this is sorta beyond the scope of the mod, but if for some reason a
      // value above 127 gets serialised as a byte by another mod's networking, this can happen
      // TODO: is 1 a safe minimum? are there any cases where somebody would want to serialise a 0 item count?
      original.call(nbt, key, (byte) Math.max(1, b));
    }
  }

  @Definition(id = "count", field = "Lnet/minecraft/item/ItemStack;count:I")
  @Definition(id = "getByte", method = "Lnet/minecraft/nbt/NbtCompound;getByte(Ljava/lang/String;)B")
  @Definition(id = "nbt", local = @Local(type = NbtCompound.class))
  @Expression("?.count = nbt.getByte('Count')")
  @WrapOperation(method = "readNbt", at = @At("MIXINEXTRAS:EXPRESSION"))
  public void readCustomCount(
    ItemStack stack,
    int value,
    Operation<Void> original,
    @Local(argsOnly = true) NbtCompound nbt
  ) {
    if (nbt.contains(nbtCountKey)) {
      stack.count = nbt.getInt(nbtCountKey);
    } else {
      // prevent ever reading a negative count
      original.call(stack, Math.max(1, value));
    }
  }
}
