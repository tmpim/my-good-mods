package pw.tmpim.gooddeathmessages.mixin;

import net.minecraft.block.CactusBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CactusBlock.class)
public class CactusBlockMixin {
  @Inject(method = "onEntityCollision", at = @At("HEAD"))
  private void onEntityCollisionHead(World world, int x, int y, int z, Entity entity, CallbackInfo ci) {
    if (entity instanceof PlayerEntity player) player.getGooddms$victim().setPricked(true);
  }

  @Inject(method = "onEntityCollision", at = @At("TAIL"))
  private void onEntityCollisionTail(World world, int x, int y, int z, Entity entity, CallbackInfo ci) {
    if (entity instanceof PlayerEntity player) player.getGooddms$victim().setPricked(false);
  }
}
