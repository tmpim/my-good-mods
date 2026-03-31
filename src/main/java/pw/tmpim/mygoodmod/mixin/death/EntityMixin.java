package pw.tmpim.mygoodmod.mixin.death;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LightningEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pw.tmpim.mygoodmod.death.Victim;

@Mixin(Entity.class)
public class EntityMixin {

  @Inject(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(I)V"))
  private void onLitBefore(double dx, double dy, double dz, CallbackInfo ci) {
    if (this instanceof Victim victim) victim.goodmod_setLit();
  }

  @Inject(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(I)V", shift = At.Shift.AFTER))
  private void onLitAfter(double dx, double dy, double dz, CallbackInfo ci) {
    if (this instanceof Victim victim) victim.goodmod_resetLit();
  }

  @Inject(method = "setOnFire", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/Entity;I)Z"))
  private void inLavaBefore(CallbackInfo ci) {
    if (this instanceof Victim victim) victim.goodmod_setLit();
  }

  @Inject(method = "setOnFire", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/Entity;I)Z", shift = At.Shift.AFTER))
  private void inLavaAfter(CallbackInfo ci) {
    if (this instanceof Victim victim) victim.goodmod_resetLit();
  }

  @Inject(method = "onStruckByLightning", at = @At("HEAD"))
  private void onStruckByLightningHead(LightningEntity lightningEntity, CallbackInfo ci) {
    if (this instanceof Victim victim) victim.goodmod_setStruck();
  }

  @Inject(method = "onStruckByLightning", at = @At("TAIL"))
  private void onStruckByLightningTail(LightningEntity lightningEntity, CallbackInfo ci) {
    if (this instanceof Victim victim) victim.goodmod_resetStruck();
  }
}
