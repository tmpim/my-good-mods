package pw.tmpim.goodcompression.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import pw.tmpim.goodcompression.GoodCompression;

@Mixin(RedstoneWireBlock.class)
public class RedstoneWireBlockMixin {
  /**
   * Allows redstone dust to be placed on top of redstone blocks, despite its material of GLASS (see RedstoneBlock.kt)
   */
  @WrapOperation(
    method = "canPlaceAt",
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/world/World;shouldSuffocate(III)Z"
    )
  )
  private boolean allowRedstoneBlockPlacement(World world, int x, int y, int z, Operation<Boolean> original) {
    if (
      Boolean.TRUE.equals(GoodCompression.getConfig().redstoneDustOnTopOfBlocks)
      && world.getBlockState(x, y, z).isIn(GoodCompression.redstoneDustPlaceable)
    ) {
      return true;
    } else {
      return original.call(world, x, y, z);
    }
  }
}
