package pw.tmpim.goodfarming.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.Block;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import pw.tmpim.goodfarming.GoodFarming;
import pw.tmpim.goodfarming.block.SaplingBlockExt;

@Mixin(DyeItem.class)
public class DyeItemMixin {
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
    Operation<Void> original
  ) {
    if (Boolean.TRUE.equals(GoodFarming.getConfig().bonemealWastageFixEnabled)) {
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
    @Local(argsOnly = true, name = "x") int x,
    @Local(argsOnly = true, name = "y") int y,
    @Local(argsOnly = true, name = "z") int z,
    @Local(argsOnly = true, name = "world") World world
  ) {
    if (Boolean.TRUE.equals(GoodFarming.getConfig().bonemealWastageFixEnabled)) {
      var blockMeta = world.getBlockMeta(x, y, z);
      return blockMeta != 7 && original.call(left, right);
    } else {
      return original.call(left, right);
    }
  }
}
