package pw.tmpim.goodfarming.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.SaplingBlock;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.Feature;
import net.modificationstation.stationapi.api.block.BlockState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pw.tmpim.goodfarming.GoodFarming;
import pw.tmpim.goodfarming.block.SaplingBlockExt;

import java.util.Random;

@Mixin(
  value = SaplingBlock.class,
  priority = 1500 // apply after StationAPI station-items-v0 SaplingBlockMixin
)
public class SaplingBlockMixin implements SaplingBlockExt {
  @Unique
  private final ThreadLocal<@NotNull Boolean> didGenerate = ThreadLocal.withInitial(() -> false);

  @Override
  public @NotNull ThreadLocal<@NotNull Boolean> getGoodfarming$didGenerate() {
    return didGenerate;
  }

  /**
   * for the non-stapi fallback mixin, track if we generated a tree successfully
    */
  @Inject(method = "generate", at = @At("HEAD"))
  public void resetDidGenerate(World world, int x, int y, int z, Random random, CallbackInfo ci) {
    didGenerate.set(false);
  }

  /**
   * for the non-stapi fallback mixin, track if we generated a tree successfully
   */
  @WrapOperation(
    method = "generate",
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/world/gen/feature/Feature;generate(Lnet/minecraft/world/World;Ljava/util/Random;III)Z"
    )
  )
  public boolean setDidGenerate(
    Feature instance,
    World world,
    Random random,
    int x,
    int y,
    int z,
    Operation<Boolean> original
  ) {
    var retVal = original.call(instance, world, random, x, y, z);
    didGenerate.set(retVal);
    return retVal;
  }

  /**
   * prevent using bone meal on saplings that don't generate. this won't lint because we're targeting an injected method
   */
  @SuppressWarnings({ "MixinAnnotationTarget", "UnresolvedMixinReference" })
  @Inject(
    method = "onBonemealUse",
    at = @At("RETURN"),
    require = 0, // allow it to fail in case the target mixin changes
    cancellable = true
  )
  public void preventBoneMealWastage(
    World world,
    int x,
    int y,
    int z,
    BlockState state,
    CallbackInfoReturnable<Boolean> cir
  ) {
    if (Boolean.TRUE.equals(GoodFarming.getConfig().bonemealWastageFixEnabled)) {
      if (!didGenerate.get()) {
        // the sapling didn't generate, don't consume the item
        cir.setReturnValue(false);
      }

      // reset didGenerate regardless
      didGenerate.set(false);
    }
  }
}
