package pw.tmpim.gooddeathmessages.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.entity.Entity;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pw.tmpim.gooddeathmessages.ExplosionTracker;
import pw.tmpim.gooddeathmessages.Victim;

@Mixin(Explosion.class)
public class ExplosionMixin {
  @Inject(method = "explode", at = @At("HEAD"))
  private void explodeHead(
    CallbackInfo ci,
    @Share("blast") LocalRef<ExplosionTracker.BlastSource> blastSource
  ) {
    blastSource.set(ExplosionTracker.popBlast());
  }

  @Inject(
    method = "explode",
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/Entity;I)Z"
    )
  )
  private void explodeDamage(
    CallbackInfo ci,
    @Local Entity entity,
    @Share("blast") LocalRef<ExplosionTracker.BlastSource> blastSource
  ) {
    var source = blastSource.get();
    if (entity instanceof Victim victim && source != null) {
      victim.getGooddms$victim().setBlastSource(source);
    }
  }

  @Inject(
    method = "explode",
    at = @At(
      value = "INVOKE_ASSIGN",
      target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/Entity;I)Z"
    )
  )
  private void explodePostDamage(
    CallbackInfo ci,
    @Local Entity entity,
    @Share("blast") LocalRef<ExplosionTracker.BlastSource> blastSource
  ) {
    var source = blastSource.get();
    if (entity instanceof Victim victim && source != null) {
      victim.getGooddms$victim().setBlastSource(null);
    }
  }
}
