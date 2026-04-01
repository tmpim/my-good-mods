package pw.tmpim.goodmod.mixin.item;

import net.minecraft.block.Block;
import net.minecraft.item.PickaxeItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PickaxeItem.class)
public interface PickaxeItemAccessor {
  @Accessor
  static void setPickaxeEffectiveBlocks(Block[] pickaxeEffectiveBlocks) {
    throw new AssertionError();
  }
}
