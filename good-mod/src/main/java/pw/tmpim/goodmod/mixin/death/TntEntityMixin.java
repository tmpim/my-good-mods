package pw.tmpim.goodmod.mixin.death;

import net.minecraft.entity.TntEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pw.tmpim.goodmod.death.ExplosionTracker;

@Mixin(TntEntity.class)
public class TntEntityMixin {
  @Inject(method = "explode", at = @At("HEAD"))
  private void explode(CallbackInfo ci) {
    ExplosionTracker.pushBlast(new ExplosionTracker.EntityBlast((TntEntity) (Object) this));
  }
}
