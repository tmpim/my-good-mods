package pw.tmpim.goodutils.mixin;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pw.tmpim.goodutils.block.FallableBlock;

@Mixin(Entity.class)
public class EntityMixin {
  @Shadow public World world;
  @Shadow public double x;
  @Shadow public double y;
  @Shadow public double z;
  @Shadow public float standingEyeHeight;
  @Shadow public float cameraOffset;

  @Shadow protected float fallDistance; /** call FallableBlock.fallOn when the entity lands on a block */
  @Inject(
    method = "fall",
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/entity/Entity;onLanding(F)V",
      shift = At.Shift.AFTER
    )
  )
  public void onFallOnBlock(double heightDifference, boolean onGround, CallbackInfo ci) {
    var bx = MathHelper.floor(x);
    var by = MathHelper.floor(y - standingEyeHeight - cameraOffset - 0.2f);
    var bz = MathHelper.floor(z);

    var block = world.getBlockId(bx, by, bz);
    if (block == 0) return; // air

    var blockClass = Block.BLOCKS[block];
    if (blockClass instanceof FallableBlock fallable) {
      fallable.goodutils$fallOn(world, bx, by, bz, (Entity) (Object) this, fallDistance);
    }
  }
}
