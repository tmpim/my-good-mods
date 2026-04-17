package pw.tmpim.goodconfig.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pw.tmpim.goodconfig.GoodConfig;
import pw.tmpim.goodconfig.files.ConfigWatcher;

@Mixin(Minecraft.class)
public class MinecraftMixin {
  @Inject(method = "stop", at = @At("HEAD"))
  public void onStop(CallbackInfo ci) {
    try {
      GoodConfig.onStop();
    } catch (Exception e) {
      GoodConfig.log.error("error stopping good-config", e);
    }
  }

  @Inject(method = "tick", at = @At("TAIL"))
  public void onTick(CallbackInfo ci) {
    try {
      if (ConfigWatcher.shouldTickClient()) {
        ConfigWatcher.processQueuedReloads();
      }
    } catch (Exception e) {
      GoodConfig.log.error("error processing config reloads", e);
    }
  }
}
