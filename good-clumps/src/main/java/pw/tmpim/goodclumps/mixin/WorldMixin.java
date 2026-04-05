package pw.tmpim.goodclumps.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pw.tmpim.goodclumps.world.WorldItemTracker;
import pw.tmpim.goodclumps.world.WorldWithItemTracker;

@Mixin(World.class)
public class WorldMixin implements WorldWithItemTracker {
  @Unique
  private WorldItemTracker itemTracker;

  @Override
  public @NotNull WorldItemTracker goodclumps_getItemTracker() {
    return itemTracker;
  }

  /**
   * initialize the item tracker
   */
  @Inject(
    method = "<init>*",
    at = @At("TAIL")
  )
  private void onInit(CallbackInfo ci) {
    itemTracker = new WorldItemTracker();
  }

  /**
   * track items when they're added
   */
  @Inject(
    method = "spawnEntity",
    at = @At("RETURN")
  )
  public void onEntityAdded(Entity entity, CallbackInfoReturnable<Boolean> cir) {
    if (cir.getReturnValueZ() && entity instanceof ItemEntity item) {
      itemTracker.addTrackedItem(item);
    }
  }

  /**
   * untrack items when they're removed.
   * TODO: is notifyEntityRemoved the best place to check? it seems to be called the most consistently (chunk unload,
   *       manual remove, entity tick, etc...)
   */
  @Inject(
    method = "notifyEntityRemoved",
    at = @At("HEAD")
  )
  public void onEntityRemoved(Entity entity, CallbackInfo ci) {
    if (entity instanceof ItemEntity item) {
      itemTracker.removeTrackedItem(item);
    }
  }
}
