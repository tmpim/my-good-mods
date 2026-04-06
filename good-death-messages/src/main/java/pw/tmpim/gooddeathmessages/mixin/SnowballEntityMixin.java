package pw.tmpim.gooddeathmessages.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SnowballEntity.class)
public class SnowballEntityMixin {

  @Inject(
    method = "tick",
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/Entity;I)Z"
    )
  )
  private void beforeHit(CallbackInfo ci, @Local(ordinal = 0) HitResult hitResult) {
    if (hitResult.entity instanceof PlayerEntity player) player.getGooddms$victim().setProjectile(true);
  }

  @Inject(
    method = "tick",
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/entity/projectile/thrown/SnowballEntity;markDead()V",
      ordinal = 1
    )
  )
  private void afterHit(CallbackInfo ci, @Local(ordinal = 0) HitResult hitResult) {
    if (hitResult.entity instanceof PlayerEntity player) player.getGooddms$victim().setProjectile(false);
  }
}
