package pw.tmpim.goodmod.mixin.death;

import net.minecraft.block.BedBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pw.tmpim.goodmod.death.ExplosionTracker;

@Mixin(BedBlock.class)
public class BedBlockMixin {
  @Inject(
    method = "onUse",
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/world/World;createExplosion(Lnet/minecraft/entity/Entity;DDDFZ)Lnet/minecraft/world/explosion/Explosion;"
    )
  )
  private void onUse(World world, int x, int y, int z, PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
    ExplosionTracker.pushBlast(new ExplosionTracker.BedBlast());
  }
}
