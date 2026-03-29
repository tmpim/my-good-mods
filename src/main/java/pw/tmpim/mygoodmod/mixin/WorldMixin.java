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

  @Inject(method = "canTransferPowerInDirection", at = @At("HEAD"), cancellable = true)
  private void goodmod$canTransferPowerInDirection(int x, int y, int z, int direction, CallbackInfoReturnable<Boolean> cir) {
    BlockState state = ((StationFlatteningWorld) this).getBlockState(x, y, z);
    if (state.getBlock() instanceof RedstoneBlock) {
      cir.setReturnValue(state.getBlock().canTransferPowerInDirection(((World) (Object) this), x, y, z, direction));
    }
  }

  @Inject(method = "isEmittingRedstonePowerInDirection", at = @At("HEAD"), cancellable = true)
  private void goodmod$isEmittingRedstonePowerInDirection(int x, int y, int z, int direction, CallbackInfoReturnable<Boolean> cir) {
    BlockState state = ((StationFlatteningWorld) this).getBlockState(x, y, z);
    if (state.getBlock() instanceof RedstoneBlock) {
      cir.setReturnValue(state.getBlock().isEmittingRedstonePowerInDirection(((World) (Object) this), x, y, z, direction));
    }
  }
}
