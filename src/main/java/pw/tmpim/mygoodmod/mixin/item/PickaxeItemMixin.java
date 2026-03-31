package pw.tmpim.mygoodmod.mixin.item;

import net.minecraft.block.Block;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pw.tmpim.mygoodmod.data.GoodPatches;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mixin(PickaxeItem.class)
public abstract class PickaxeItemMixin extends ToolItem {
  @Shadow
  private static Block[] pickaxeEffectiveBlocks;

  public PickaxeItemMixin(int id, int damageBoost, ToolMaterial toolMaterial, Block[] effectiveOn) {
    super(id, damageBoost, toolMaterial, effectiveOn);
  }

  @Inject(
    method = "<clinit>()V",
    at = @At("TAIL")
  )
  private static void goodmod$addEffectiveBlocks(CallbackInfo ci) {
    List<Block> blocks = new ArrayList<>(Arrays.asList(pickaxeEffectiveBlocks));
    blocks.addAll(GoodPatches.getPickaxeEffective());
    PickaxeItemAccessor.setPickaxeEffectiveBlocks(blocks.toArray(Block[]::new));
  }
}
