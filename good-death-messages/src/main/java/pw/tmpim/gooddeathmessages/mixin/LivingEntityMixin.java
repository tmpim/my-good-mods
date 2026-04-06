package pw.tmpim.gooddeathmessages.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pw.tmpim.gooddeathmessages.Victim;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

  public LivingEntityMixin(World world) {
    super(world);
  }

  @ModifyReturnValue(method = "isOnLadder", at = @At("RETURN"))
  private boolean isOnLadder(boolean original) {
    if (original && this instanceof Victim victim) {
      Victim.Data data = victim.getGooddms$victim();
      if (!data.getWasClimbing()) data.setWasClimbing(true);
    }
    return original;
  }

  @Inject(method = "tick", at = @At("TAIL"))
  private void afterTick(CallbackInfo ci) {
    if (onGround && this instanceof Victim victim) {
      Victim.Data data = victim.getGooddms$victim();
      if (data.getWasClimbing()) data.setWasClimbing(false);
    }
  }
}
