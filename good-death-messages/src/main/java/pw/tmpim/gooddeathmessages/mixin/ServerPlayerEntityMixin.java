package pw.tmpim.gooddeathmessages.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pw.tmpim.gooddeathmessages.DeathRegistry;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
  @Shadow
  public MinecraftServer server;

  public ServerPlayerEntityMixin(World world) {
    super(world);
  }

  @Inject(method = "onKilledBy", at = @At("TAIL"))
  private void onKilledBy(Entity killer, CallbackInfo ci) {
    var message = DeathRegistry.createMessage(this, killer);
    server.sendMessage(message);
    server.playerManager.broadcast(message);
  }
}
