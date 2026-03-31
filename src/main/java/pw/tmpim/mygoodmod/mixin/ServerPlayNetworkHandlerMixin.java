package pw.tmpim.mygoodmod.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pw.tmpim.mygoodmod.MyGoodMod;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
  @Shadow
  private ServerPlayerEntity player;

  @Unique
  private String initialCommand;

  /**
   * For the /tell mixin, capture the initial command before its value is replaced internally.
   */
  @Inject(
    method = "handleCommand",
    at = @At("HEAD")
  )
  public void captureCommand(String command, CallbackInfo ci) {
    initialCommand = command;
  }

  /**
   * When a user runs `/tell [player] [message]`, also show the message to the sender.
   */
  @Inject(
    method = "handleCommand",
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/server/PlayerManager;sendPacket(Ljava/lang/String;Lnet/minecraft/network/packet/Packet;)Z"
    )
  )
  public void onSendTellMessage(
    String message,
    CallbackInfo ci,
    @Local String[] var2
  ) {
    try {
      var src = player.name;
      var dst = var2[1];
      var msg = initialCommand.substring(initialCommand.indexOf(" ")).trim();
      msg = msg.substring(msg.indexOf(" ")).trim();
      var newMessage = "§7" + src + " -> " + dst + ": " + msg;

      player.sendMessage(newMessage);
    } catch (Exception e) {
      MyGoodMod.log.error("failed to forward /tell to sender", e);
    }
  }
}
