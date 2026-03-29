package pw.tmpim.mygoodmod.mixin;

import net.minecraft.block.Block;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.item.ShovelItem.class)
public interface ShovelItemAccessor {
  @Accessor
  static void setShovelEffectiveBlocks(Block[] shovelEffectiveBlocks) {
    throw new UnsupportedOperationException();
  }
}
