package pw.tmpim.mygoodmod.mixin;

import net.minecraft.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(net.minecraft.item.PickaxeItem.class)
public interface PickaxeItemAccessor {
  @Accessor
  static void setPickaxeEffectiveBlocks(Block[] pickaxeEffectiveBlocks) {
    throw new AssertionError();
  }
}
