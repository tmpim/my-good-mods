package pw.tmpim.goodutils.mixin;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pw.tmpim.goodutils.block.OnPlaceItemStack;

@Mixin(BlockItem.class)
class BlockItemMixin {
  @Shadow
  private int blockId;

  @Inject(
    method = "useOnBlock",
    at = @At("RETURN")
  )
  public void onPlacedWithItemStack(
    ItemStack stack,
    PlayerEntity user,
    World world,
    int x, int y, int z,
    int side,
    CallbackInfoReturnable<Boolean> cir
  ) {
    if (cir.getReturnValue()) {
      // Block was placed
      if (Block.BLOCKS[this.blockId] instanceof OnPlaceItemStack block) {
        block.onPlaced(world, x, y, z, stack);
      }
    }
  }
}
