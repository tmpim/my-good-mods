package pw.tmpim.mygoodmod.mixin;

import net.minecraft.block.FlowingLiquidBlock;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(FlowingLiquidBlock.class)
public class FlowingLiquidBlockMixin {
  @Inject(
    method = "onTick",
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/world/World;setBlock(IIIII)Z"
    )
  )
  public void goodmod$convertToSource(World world, int x, int y, int z, Random random, CallbackInfo ci) {
    world
      .getBlockState(x, y - 1, z)
      .getBlock()
      .dropStacks(world, x, y - 1, z, world.getBlockMeta(x, y - 1, z));
  }
}
