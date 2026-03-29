package pw.tmpim.mygoodmod.mixin;

import net.minecraft.block.Block;
import net.minecraft.item.ShovelItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ShovelItem.class)
public interface ShovelItemAccessor {
  @Accessor
  static void setShovelEffectiveBlocks(Block[] shovelEffectiveBlocks) {
    throw new UnsupportedOperationException();
  }
}
