package pw.tmpim.mygoodmod.mixin.item;

import net.minecraft.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(net.minecraft.item.AxeItem.class)
public interface AxeItemAccessor {
  @Accessor
  static void setAxeEffectiveBlocks(Block[] axeEffectiveBlocks) {
    throw new UnsupportedOperationException();
  }
}
