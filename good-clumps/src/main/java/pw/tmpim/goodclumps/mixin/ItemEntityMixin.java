package pw.tmpim.goodclumps.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pw.tmpim.goodclumps.GoodClumps;
import pw.tmpim.goodclumps.entity.TrackedItemEntity;
import pw.tmpim.goodclumps.world.WorldWithItemTracker;

@Mixin(ItemEntity.class)
public class ItemEntityMixin implements TrackedItemEntity {
  @Shadow
  public ItemStack stack;

  @Unique
  private long trackedSectionKey = Long.MIN_VALUE; // sentinel for "not yet tracked"

  @Unique
  private ItemStack trackedStack = null;

  @Override
  public long goodclumps_getTrackedSectionKey() {
    return trackedSectionKey;
  }

  @Override
  public void goodclumps_setTrackedSectionKey(long key) {
    trackedSectionKey = key;
  }

  @Override
  public @Nullable ItemStack goodclumps_getTrackedStack() {
    return trackedStack;
  }

  @Override
  public void goodclumps_setTrackedStack(@Nullable ItemStack stack) {
    trackedStack = stack;
  }

  /**
   * after the item has moved, but before it ticks its age/marks itself dead, track the item in the item merging
   * tracker, and perform item merging
   */
  @Definition(id = "itemTicks", field = "Lnet/minecraft/entity/ItemEntity;itemTicks:I")
  @Expression("?.itemTicks = ?.itemTicks + 1")
  @Inject(method = "tick", at = @At("MIXINEXTRAS:EXPRESSION"))
  public void afterMove(CallbackInfo ci) {
    var item = (ItemEntity) (Object) this;
    if (item.world.isRemote) return; // don't run on clientside

    @SuppressWarnings("RedundantCast") // compileJava mixin compilation doesn't know about classtweaker
    var tracker = ((WorldWithItemTracker) item.world).getGoodclumps$itemTracker();

    // track the item. for now, still do this even if merging is disabled, as it can be re-enabled at runtime, so let's
    // avoid having to retroactively start tracking every entity
    tracker.updateTrackedItem(item);

    // perform item merging if it's time
    if (GoodClumps.getEnabled() && TrackedItemEntity.isTimeToMerge(item)) {
      tracker.mergeWithNeighbours(item);
    }
  }

  /**
   * this bugfix may technically be out of scope, but clear up the item entities if their stack is empty. modern vanilla
   * does this now.
   */
  @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
  public void cleanEmptyItems(CallbackInfo ci) {
    if (stack == null || stack.count <= 0) {
      ((ItemEntity) (Object) this).markDead(); // no isRemote check here should be fine
      ci.cancel();
    }
  }

  /**
   * another vanilla bugfix: exit early on pickup if the entity's stack is empty
   */
  @Inject(
    method = "onPlayerInteraction",
    at = @At(
      value = "FIELD",
      target = "Lnet/minecraft/entity/ItemEntity;pickupDelay:I",
      opcode = Opcodes.GETFIELD
    ),
    cancellable = true
  )
  public void preventEmptyPickup(PlayerEntity player, CallbackInfo ci) {
    if (stack == null || stack.count <= 0) {
      ((ItemEntity) (Object) this).markDead();
      ci.cancel();
    }
  }
}
