package pw.tmpim.goodfarming.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pw.tmpim.goodfarming.block.SaplingBlockExt;
import pw.tmpim.goodfarming.config.TweaksConfig;
import pw.tmpim.goodfarming.item.DyeItemExt;

@SuppressWarnings("LocalMayUseName") // not on obfuscated code it can't lol
@Mixin(DyeItem.class)
public class DyeItemMixin implements DyeItemExt {
  @Unique
  private final ThreadLocal<@Nullable PlayerEntity> usingPlayer = ThreadLocal.withInitial(() -> null);

  @Override
  public @NotNull ThreadLocal<@Nullable PlayerEntity> getGoodfarming$usingPlayer() {
    return usingPlayer;
  }

  /**
   * track the using player, because stapi's bonemeal API doesn't pass it down lol
   */
  @Inject(method = "useOnBlock", at = @At("HEAD"))
  public void trackUsingPlayer(
    ItemStack item,
    PlayerEntity user,
    World world,
    int x,
    int y,
    int z,
    int side,
    CallbackInfoReturnable<Boolean> cir
  ) {
    usingPlayer.set(user);
  }

  @Inject(method = "useOnBlock", at = @At("TAIL"))
  public void untrackUsingPlayer(
    ItemStack item,
    PlayerEntity user,
    World world,
    int x,
    int y,
    int z,
    int side,
    CallbackInfoReturnable<Boolean> cir
  ) {
    usingPlayer.set(null);
  }

  /**
   * prevent using bone meal on saplings that can't grow (doesn't affect StationBlock.onBonemealUse or BonemealAPI).
   * sadly, this one only runs serverside, so we can't return the correct arm swing value
   */
  @WrapOperation(
    method = "useOnBlock",
    at = @At(
      value = "FIELD",
      opcode = Opcodes.PUTFIELD,
      target = "Lnet/minecraft/item/ItemStack;count:I",
      ordinal = 0
    )
  )
  public void preventBoneMealWastageOnSaplings(
    ItemStack instance,
    int value,
    Operation<Void> original,
    @Local(argsOnly = true) PlayerEntity user
  ) {
    if (TweaksConfig.INSTANCE.isBonemealWastageFixEnabled(user)) {
      var didGenerate = ((SaplingBlockExt) Block.SAPLING).getGoodfarming$didGenerate();
      if (didGenerate.get()) {
        // the sapling generated successfully, call the original count decrement
        original.call(instance, value);
      }

      // reset didGenerate regardless
      didGenerate.set(false);
    } else {
      original.call(instance, value);
    }
  }

  /**
   * prevent using bone meal on wheat if it's fully grown (doesn't affect StationBlock.onBonemealUse or BonemealAPI)
   */
  @SuppressWarnings("LocalMayUseName") // no it can't. the targets are obfuscated
  @Definition(id = "WHEAT", field = "Lnet/minecraft/block/Block;WHEAT:Lnet/minecraft/block/Block;")
  @Definition(id = "id", field = "Lnet/minecraft/block/Block;id:I")
  @Expression("? == WHEAT.id")
  @WrapOperation(
    method = "useOnBlock",
    at = @At("MIXINEXTRAS:EXPRESSION")
  )
  public boolean preventBoneMealWastageOnWheat(
    int left,
    int right,
    Operation<Boolean> original,
    @Local(argsOnly = true, ordinal = 0) int x,
    @Local(argsOnly = true, ordinal = 1) int y,
    @Local(argsOnly = true, ordinal = 2) int z,
    @Local(argsOnly = true) World world,
    @Local(argsOnly = true) PlayerEntity user
  ) {
    if (TweaksConfig.INSTANCE.isBonemealWastageFixEnabled(user)) {
      var blockMeta = world.getBlockMeta(x, y, z);
      return blockMeta != 7 && original.call(left, right);
    } else {
      return original.call(left, right);
    }
  }
}
