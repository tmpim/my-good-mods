package pw.tmpim.goodfarming.mixin;

import kotlin.jvm.functions.Function1;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pw.tmpim.goodfarming.world.WorldExt;

/**
 * here be demons pls dont look
 */
@Mixin(World.class)
public class WorldMixin implements WorldExt {
  @Shadow
  public boolean isRemote;
  
  @Unique
  private final ThreadLocal<@Nullable Function1<@NotNull ItemStack, @NotNull Boolean>> capturingItemSpawns =
    ThreadLocal.withInitial(() -> null);

  @Override
  public @NotNull ThreadLocal<@Nullable Function1<@NotNull ItemStack, @NotNull Boolean>> getGoodfarming$capturingItemSpawns() {
    return capturingItemSpawns;
  }

  @Inject(
    method = "spawnEntity",
    at = @At("HEAD"),
    cancellable = true
  )
  public void captureItemsIfNecessary(Entity entity, CallbackInfoReturnable<Boolean> cir) {
    if (isRemote) return;

    var fn = capturingItemSpawns.get();
    if (fn == null || !(entity instanceof ItemEntity itemEntity)) return;

    var stack = itemEntity.stack;
    if (stack == null) return; // hopefully not lol

    if (fn.invoke(stack) || stack.count <= 0) {
      cir.setReturnValue(false);
    }
  }
}
