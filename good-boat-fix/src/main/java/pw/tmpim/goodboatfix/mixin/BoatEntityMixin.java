package pw.tmpim.goodboatfix.mixin;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import pw.tmpim.goodboatfix.GoodBoatFix;

@Mixin(BoatEntity.class)
public abstract class BoatEntityMixin extends Entity {
  public BoatEntityMixin(World world) {
    super(world);
  }

  /**
   * Drops the boat item when a boat gets destroyed, instead of sticks and planks. May conflict with UniTweaks.
   */
  @Override
  public void markDead() {
    super.markDead();
    if (GoodBoatFix.shouldApplyMixin()) {
      dropItem(Item.BOAT.id, 1, 0.0f);
    }
  }

  @Override
  public ItemEntity dropItem(ItemStack itemStack, float yOffset) {
    if (GoodBoatFix.shouldApplyMixin()) {
      var id = itemStack.getItem().id;

      if (id == Block.PLANKS.id || id == Item.STICK.id) {
        return null;
      }
    }

    return super.dropItem(itemStack, yOffset);
  }
}
