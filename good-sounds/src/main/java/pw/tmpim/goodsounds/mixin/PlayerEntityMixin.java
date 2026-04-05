package pw.tmpim.goodsounds.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pw.tmpim.goodsounds.GoodSounds;

import java.util.Objects;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
  @Shadow
  public abstract ItemStack getHand();

  public PlayerEntityMixin(World world) {
    super(world);
  }

  @Inject(
    method = "attack",
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/Entity;I)Z"
    )
  )
  private void playMetalPipeOnHit(Entity target, CallbackInfo cbi) {
    if (world.isRemote) {
      return;
    }

    var config = GoodSounds.getConfig();
    if (!Objects.equals(config.metalPipeOnHit, Boolean.TRUE)) {
      return;
    }

    var held = getHand();
    if (held == null) {
      return;
    }

    if (!GoodSounds.isMetalItem(held.getItem())) {
      return;
    }

    if (target instanceof LivingEntity && target.isAlive()) {
      var volume = config.metalPipeVolume == null ? GoodSounds.DEFAULT_METAL_PIPE_VOLUME : config.metalPipeVolume;
      world.playSound(this, GoodSounds.SOUND_METAL_PIPE, volume, 1.0f);
    }
  }
}
