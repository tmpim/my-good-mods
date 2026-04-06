package pw.tmpim.gooddeathmessages.mixin;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
  @Shadow
  private ServerPlayerEntity player;

  @Inject(
    method = "handleCommand",
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/entity/player/ServerPlayerEntity;damage(Lnet/minecraft/entity/Entity;I)Z"
    )
  )
  private void handleCommandBefore(String message, CallbackInfo ci) {
    player.getGooddms$victim().setKillCommand(true);
  }

  @Inject(
    method = "handleCommand",
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/entity/player/ServerPlayerEntity;damage(Lnet/minecraft/entity/Entity;I)Z",
      shift = At.Shift.AFTER
    )
  )
  private void handleCommandAfter(String message, CallbackInfo ci) {
    player.getGooddms$victim().setKillCommand(false);
  }
}
