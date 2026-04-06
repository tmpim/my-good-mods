package pw.tmpim.gooddeathmessages.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArrowEntity.class)
public abstract class ArrowEntityMixin extends Entity {

  public ArrowEntityMixin(World world) {
    super(world);
  }

  @Inject(
    method = "tick",
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/Entity;I)Z"
    )
  )
  private void tickShot(CallbackInfo ci, @Local(ordinal = 0) HitResult hit) {
    if (hit.entity instanceof PlayerEntity player) player.getGooddms$victim().setShotBy((ArrowEntity) (Object) this);
  }

  @Inject(
    method = "tick", at = {
      @At(
        value = "INVOKE",
        target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/Entity;Ljava/lang/String;FF)V"
      ),
      @At(
        value = "FIELD",
        target = "Lnet/minecraft/entity/projectile/ArrowEntity;inAirTime:I",
        opcode = Opcodes.PUTFIELD,
        ordinal = 2
      )
    }
  )
  private void resetShot(CallbackInfo ci, @Local(ordinal = 0) HitResult hit) {
    if (hit.entity instanceof PlayerEntity player) player.getGooddms$victim().setShotBy(null);
  }
}
