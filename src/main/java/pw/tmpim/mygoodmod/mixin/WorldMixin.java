package pw.tmpim.mygoodmod.mixin;

import net.minecraft.world.World;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.world.StationFlatteningWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pw.tmpim.mygoodmod.block.RedstoneBlock;

@Mixin(World.class)
public class WorldMixin {
  /**
   * Allows redstone blocks to transmit redstone.
   * NB: StationAPI currently does not override this for all StationBlock implementations, but when it does, this will
   *     become redundant. We currently don't target all StationBlocks here to err on the side of caution.
   */
  @Inject(method = "canTransferPowerInDirection", at = @At("HEAD"), cancellable = true)
  private void canTransferPowerInDirection(
    int x,
    int y,
    int z,
    int direction,
    CallbackInfoReturnable<Boolean> cir
  ) {
    var state = ((StationFlatteningWorld) this).getBlockState(x, y, z);
    if (state.getBlock() instanceof RedstoneBlock) {
      var world = ((World) (Object) this);
      cir.setReturnValue(state.getBlock().canTransferPowerInDirection(world, x, y, z, direction));
    }
  }

  /**
   * Allows redstone blocks to transmit redstone.
   * NB: StationAPI currently does not override this for all StationBlock implementations, but when it does, this will
   *     become redundant. We currently don't target all StationBlocks here to err on the side of caution.
   */
  @Inject(method = "isEmittingRedstonePowerInDirection", at = @At("HEAD"), cancellable = true)
  private void isEmittingRedstonePowerInDirection(
    int x,
    int y,
    int z,
    int direction,
    CallbackInfoReturnable<Boolean> cir
  ) {
    var state = ((StationFlatteningWorld) this).getBlockState(x, y, z);
    if (state.getBlock() instanceof RedstoneBlock) {
      var world = ((World) (Object) this);
      cir.setReturnValue(state.getBlock().isEmittingRedstonePowerInDirection(world, x, y, z, direction));
    }
  }
}
