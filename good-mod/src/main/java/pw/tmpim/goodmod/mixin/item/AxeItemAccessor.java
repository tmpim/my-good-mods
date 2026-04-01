package pw.tmpim.goodmod.mixin.item;

import net.minecraft.block.Block;
import net.minecraft.item.AxeItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AxeItem.class)
public interface AxeItemAccessor {
  @Accessor
  static void setAxeEffectiveBlocks(Block[] axeEffectiveBlocks) {
    throw new UnsupportedOperationException();
  }
}
