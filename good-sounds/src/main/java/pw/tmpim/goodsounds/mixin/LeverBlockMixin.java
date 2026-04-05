package pw.tmpim.goodsounds.mixin;

import net.minecraft.block.LeverBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LeverBlock.class)
public class LeverBlockMixin {
  @Inject(method = "onUse", at = @At("HEAD"))
  private void playClickSound(World world, int x, int y, int z, PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
    if (world.isRemote) {
      int meta = world.getBlockMeta(x, y, z);
      int state = 8 - (meta & 8);
      world.playSound(x + 0.5, y + 0.5, z + 0.5, "random.click", 0.3F, state > 0 ? 0.6F : 0.5F);
    }
  }
}
