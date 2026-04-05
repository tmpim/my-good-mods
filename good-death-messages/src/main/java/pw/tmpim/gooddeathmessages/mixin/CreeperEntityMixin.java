package pw.tmpim.gooddeathmessages.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.MonsterEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pw.tmpim.gooddeathmessages.ExplosionTracker;

@Mixin(CreeperEntity.class)
public abstract class CreeperEntityMixin extends MonsterEntity {

  public CreeperEntityMixin(World world) {
    super(world);
  }

  @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/CreeperEntity;isCharged()Z"))
  private void beforeExplosion(Entity other, float distance, CallbackInfo ci) {
    ExplosionTracker.pushBlast(new ExplosionTracker.EntityBlast(this));
  }
}
