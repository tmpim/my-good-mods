package pw.tmpim.gooddeath.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import pw.tmpim.gooddeath.block.TombstoneBlock;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
  public ServerPlayerEntityMixin() {}

  @WrapOperation(
    method = "onKilledBy",
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/entity/player/PlayerInventory;dropInventory()V"
    )
  )
  private void onDropInventoryAfterDeath(PlayerInventory instance, Operation<Void> original) {
    TombstoneBlock.spawnForDeadPlayer((PlayerEntity) (Object) this);
  }
}
