package pw.tmpim.goodfarming.mixin;

import net.minecraft.client.InteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pw.tmpim.goodfarming.block.QuickReplanting;
import pw.tmpim.goodfarming.config.TweaksConfig;

@Mixin(InteractionManager.class)
public class InteractionManagerMixin {
  /**
   * if a right-click interaction on a block fails (either with hand or an item), see if we can delegate it to quick
   * replanting
   */
  @Inject(
    method = "interactBlock",
    at = @At("RETURN"),
    cancellable = true
  )
  public void attemptQuickReplant(
    PlayerEntity player,
    World world,
    ItemStack item,
    int x,
    int y,
    int z,
    int side,
    CallbackInfoReturnable<Boolean> cir
  ) {
    if (
      !cir.getReturnValueZ()
      && TweaksConfig.INSTANCE.isQuickReplantingEnabled(player)
      && QuickReplanting.attemptQuickReplanting(player, world, x, y, z)
    ) {
      cir.setReturnValue(true);
    }
  }
}
