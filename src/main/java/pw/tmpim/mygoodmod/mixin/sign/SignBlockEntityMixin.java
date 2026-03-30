package pw.tmpim.mygoodmod.mixin.sign;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SignBlockEntity.class)
public class SignBlockEntityMixin extends BlockEntity {
  @Shadow
  private boolean editable;

  @Inject(method = "readNbt", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/BlockEntity;readNbt(Lnet/minecraft/nbt/NbtCompound;)V"))
  private void readNbt(NbtCompound par1, CallbackInfo ci) {
    this.editable = true;
  }

  @Override
  public void markDirty() {
    this.editable = true;
    super.markDirty();
  }

}
