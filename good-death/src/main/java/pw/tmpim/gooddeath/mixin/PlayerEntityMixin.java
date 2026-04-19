package pw.tmpim.gooddeath.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import pw.tmpim.gooddeath.GoodDeath;
import pw.tmpim.gooddeath.block.TombstoneBlock;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin extends LivingEntity {
  public PlayerEntityMixin(World world) {
    super(world);
  }

  @WrapOperation(
    method = "onKilledBy",
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/entity/player/PlayerInventory;dropInventory()V"
    )
  )
  private void onDropInventoryAfterDeath(PlayerInventory instance, Operation<Void> original) {
    TombstoneBlock.spawnTombstoneForDeadPlayer((PlayerEntity) (Object) this);
  }
}
