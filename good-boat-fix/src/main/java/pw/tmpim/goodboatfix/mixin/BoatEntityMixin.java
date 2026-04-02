package pw.tmpim.goodboatfix.mixin;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pw.tmpim.goodboatfix.GoodBoatFix;

@Mixin(BoatEntity.class)
public abstract class BoatEntityMixin extends Entity {
  public BoatEntityMixin(World world) {
    super(world);
  }

  /**
   * Drops the boat item when breaking a boat, instead of sticks and planks. May conflict with UniTweaks.
   */
  @Inject(
    method = "damage",
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/entity/vehicle/BoatEntity;markDead()V"
    )
  )
  public void dropBoatItem(Entity damageSource, int amount, CallbackInfoReturnable<Boolean> cir) {
    if (GoodBoatFix.shouldApplyMixin()) {
      dropItem(Item.BOAT.id, 1, 0.0f);
    }
  }

  @Override
  public ItemEntity dropItem(ItemStack itemStack, float yOffset) {
    if (GoodBoatFix.shouldApplyMixin()) {
      var id = itemStack.getItem().id;

      if (id == Block.PLANKS.id || id == Item.STICK.id) {
        return null;
      }
    }

    return super.dropItem(itemStack, yOffset);
  }
}
