package pw.tmpim.goodstacks.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pw.tmpim.goodstacks.GoodStacksClient;

@Mixin(Minecraft.class)
public class MinecraftMixin {
  @Inject(
    method = "startGame",
    at = @At("HEAD")
  )
  public void onGameStart(String worldName, String name, long seed, CallbackInfo ci) {
    GoodStacksClient.onGameStart();
  }
}
