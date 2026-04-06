package pw.tmpim.gooddeathmessages.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import pw.tmpim.gooddeathmessages.DeathRegistry;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends PlayerEntity {
  @Shadow
  protected Minecraft minecraft;

  public ClientPlayerEntityMixin(World world) {
    super(world);
  }

  @Override
  public void onKilledBy(Entity killer) {
    var message = DeathRegistry.createMessage(this, killer);
    if (message == null) return;
    
    minecraft.inGameHud.addChatMessage(message);
  }
}
