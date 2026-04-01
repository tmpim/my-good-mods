package pw.tmpim.goodmod.mixin.block;

import net.minecraft.block.FlowingLiquidBlock;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(FlowingLiquidBlock.class)
public class FlowingLiquidBlockMixin {
  /**
   * Fixes a bug where flowing liquid blocks from above will destroy blocks such as torches and rails without dropping
   * their items.
   */
  @Inject(
    method = "onTick",
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/world/World;setBlock(IIIII)Z"
    )
  )
  public void convertToSource(World world, int x, int y, int z, Random random, CallbackInfo ci) {
    world
      .getBlockState(x, y - 1, z)
      .getBlock()
      .dropStacks(world, x, y - 1, z, world.getBlockMeta(x, y - 1, z));
  }
}
