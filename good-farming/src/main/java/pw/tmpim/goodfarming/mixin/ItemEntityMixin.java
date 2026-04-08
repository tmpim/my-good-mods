package pw.tmpim.goodfarming.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import pw.tmpim.goodfarming.config.TweaksConfig;
import pw.tmpim.goodfarming.item.SeedPickup;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {
  /** attempt to place picked up seeds in the player's Seed Bags */
  @WrapOperation(
    method = "onPlayerInteraction",
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/entity/player/PlayerInventory;addStack(Lnet/minecraft/item/ItemStack;)Z"
    )
  )
  public boolean onItemPickup(
    PlayerInventory inv,
    ItemStack stack,
    Operation<Boolean> original,
    @Local(argsOnly = true) PlayerEntity player
  ) {
    return (
      TweaksConfig.INSTANCE.isSeedBagAutoPickupEnabled(player)
      && SeedPickup.onItemPickup(inv, stack)
    ) || original.call(inv, stack);
  }
}
