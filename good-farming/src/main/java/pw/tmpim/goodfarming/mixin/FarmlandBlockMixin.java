package pw.tmpim.goodfarming.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pw.tmpim.goodfarming.GoodFarming;
import pw.tmpim.goodutils.block.FallableBlock;

@Mixin(FarmlandBlock.class)
public class FarmlandBlockMixin implements FallableBlock {
  /** if trampling nerf is enabled, remove the vanilla steppedOn check */
  @Inject(method = "onSteppedOn", at = @At("HEAD"), cancellable = true)
  public void preventVanillaTrampleBehaviour(CallbackInfo ci) {
    if (Boolean.TRUE.equals(GoodFarming.getConfig().tramplingNerfEnabled)) {
      ci.cancel();
    }
  }

  @Override
  public void goodutils$fallOn(
    @NotNull World world,
    int x,
    int y,
    int z,
    @NotNull Entity entity,
    double fallDistance
  ) {
    if (
      Boolean.TRUE.equals(GoodFarming.getConfig().tramplingNerfEnabled)
      && !world.isRemote
      // cap the fall distance to trigger & introduce randomness
      && world.random.nextFloat() < fallDistance - 0.5
      // ensure the entity is big enough to trample
      && entity.width * entity.width * entity.height > 0.512f
    ) {
      world.setBlock(x, y, z, Block.DIRT.id);
    }
  }
}
