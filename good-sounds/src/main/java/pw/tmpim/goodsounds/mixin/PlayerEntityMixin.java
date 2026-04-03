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
import pw.tmpim.goodsounds.MetalItems;

import java.util.Objects;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends Entity {
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
    if (!Objects.equals(config.metalPipe, Boolean.TRUE)) {
      return;
    }

    var hand = getHand();
    var metalItems = MetalItems.INSTANCE.getMetalItemSet();
    if (hand == null || !metalItems.contains(hand.getItem())) {
      return;
    }

    if (target instanceof LivingEntity && target.isAlive()) {
      world.playSound(this, GoodSounds.MOD_ID + ":metal_pipe", 1.0f, 1.0f);
    }
  }
}
