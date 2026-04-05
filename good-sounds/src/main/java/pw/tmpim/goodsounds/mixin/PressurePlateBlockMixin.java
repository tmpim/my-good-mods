package pw.tmpim.goodsounds.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.block.Block;
import net.minecraft.block.PressurePlateBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(PressurePlateBlock.class)
public abstract class PressurePlateBlockMixin {
  @Shadow
  protected abstract void updatePlateState(World world, int x, int y, int z);

  @Inject(method = "onEntityCollision", at = @At("HEAD"))
  private void playClickOnPress(World world, int x, int y, int z, Entity entity, CallbackInfo ci) {
    // The !world.isRemote case is already handled, but we also need this to happen on the client.
    // The "proper" way would be to remove the if statement, but I think this is the most mixin-friendly way?
    if (world.isRemote) {
      if (world.getBlockMeta(x, y, z) != 1) {
        this.updatePlateState(world, x, y, z);
      }
    }
  }

  @Inject(method = "onTick", at = @At("HEAD"))
  private void playClickOnTick(World world, int x, int y, int z, Random random, CallbackInfo ci) {
    // See onClientEntityCollision.
    if (world.isRemote) {
      if (world.getBlockMeta(x, y, z) != 0) {
        this.updatePlateState(world, x, y, z);
      }
    }
  }

  @WrapWithCondition(
    method = "updatePlateState",
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/world/World;setBlocksDirty(IIIIII)V"
    )
  )
  private boolean shouldSetBlocksDirty(World world, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
    return !world.isRemote;
  }

  @WrapWithCondition(
    method = "updatePlateState",
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/world/World;notifyNeighbors(IIII)V"
    )
  )
  private boolean shouldNotifyNeighbours(World world, int x, int y, int z, int blockId) {
    return !world.isRemote;
  }

  @WrapWithCondition(
    method = "updatePlateState",
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/world/World;scheduleBlockUpdate(IIIII)V"
    )
  )
  private boolean shouldScheduleBlockUpdate(World world, int x, int y, int z, int id, int tickRate) {
    return !world.isRemote;
  }
}
