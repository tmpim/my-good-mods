package pw.tmpim.mygoodmod.mixin;

import net.minecraft.block.Block;
import net.minecraft.item.AxeItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pw.tmpim.mygoodmod.data.GoodPatches;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mixin(AxeItem.class)
public class AxeItemMixin {

  @Shadow
  private static Block[] axeEffectiveBlocks;

  @Inject(
    method = "<clinit>()V",
    at = @At("TAIL")
  )
  private static void goodmod$addEffectiveBlocks(CallbackInfo ci) {
    List<Block> blocks = new ArrayList<>(Arrays.asList(axeEffectiveBlocks));
    blocks.addAll(GoodPatches.getAxeEffective());
    AxeItemAccessor.setAxeEffectiveBlocks(blocks.toArray(Block[]::new));
  }
}
