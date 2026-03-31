package pw.tmpim.mygoodmod.mixin.death;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import pw.tmpim.mygoodmod.death.PlayerEntityDeathHandler;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends PlayerEntity {
  @Shadow
  protected Minecraft minecraft;

  public ClientPlayerEntityMixin(World world) {
    super(world);
  }

  @Override
  public void onKilledBy(Entity killer) {
    minecraft.inGameHud.addChatMessage(PlayerEntityDeathHandler.createMessage(this, killer));
  }
}
