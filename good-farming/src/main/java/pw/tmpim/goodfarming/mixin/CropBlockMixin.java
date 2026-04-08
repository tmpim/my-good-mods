package pw.tmpim.goodfarming.mixin;

import net.minecraft.block.CropBlock;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.block.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pw.tmpim.goodfarming.config.TweaksConfig;
import pw.tmpim.goodfarming.item.DyeItemExt;

@Mixin(
  value = CropBlock.class,
  priority = 1500 // apply after StationAPI station-items-v0 CropBlockMixin
)
public class CropBlockMixin {
  /**
   * prevent using bone meal on wheat if it's fully grown. this won't lint because we're targeting an injected method
   */
  @SuppressWarnings({ "MixinAnnotationTarget", "UnresolvedMixinReference" })
  @Inject(
    method = "onBonemealUse",
    at = @At("HEAD"),
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
    var user = ((DyeItemExt) Item.DYE).getGoodfarming$usingPlayer().get();
    if (user != null && TweaksConfig.INSTANCE.isBonemealWastageFixEnabled(user)) {
      if (world.getBlockMeta(x, y, z) == 7) {
        cir.setReturnValue(false);
      }
    }
  }
}
