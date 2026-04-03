package pw.tmpim.goodsounds.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pw.tmpim.goodsounds.GoodSounds;
import pw.tmpim.goodsounds.MetalItems;

import java.util.Objects;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
  public LivingEntityMixin(World world) {
    super(world);
  }

  @Inject(
    method = "onLanding",
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/entity/Entity;I)Z"
    )
  )
  private void playMetalPipeOnFall(float fallDistance, CallbackInfo cbi) {
    if (world.isRemote) {
      return;
    }

    var config = GoodSounds.getConfig();
    if (!Objects.equals(config.metalPipe, Boolean.TRUE)) {
      return;
    }

    var obj = (Object)this;
    if (obj instanceof PlayerEntity player) {
      var boots = player.inventory.getArmorStack(0);
      if (boots == null) {
        return;
      }

      var metalItems = MetalItems.INSTANCE.getMetalItemSet();
      if (metalItems.contains(boots.getItem())) {
        world.playSound(this, GoodSounds.MOD_ID + ":metal_pipe", 1.0f, 1.0f);
      }
    }
  }
}
