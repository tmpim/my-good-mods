package pw.tmpim.gooddeathmessages.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pw.tmpim.gooddeathmessages.ExplosionTracker;

@Mixin(FireballEntity.class)
public abstract class FireballEntityMixin extends Entity {
  public FireballEntityMixin(World world) {
    super(world);
  }

  @Inject(
    method = "tick",
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/world/World;createExplosion(Lnet/minecraft/entity/Entity;DDDFZ)Lnet/minecraft/world/explosion/Explosion;"
    )
  )
  private void tick(CallbackInfo ci) {
    ExplosionTracker.pushBlast(new ExplosionTracker.EntityBlast(this));
  }
}
