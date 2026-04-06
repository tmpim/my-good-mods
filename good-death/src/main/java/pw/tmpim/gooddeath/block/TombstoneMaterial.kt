package pw.tmpim.gooddeath.block

import net.minecraft.block.MapColor
import net.minecraft.block.material.Material

class TombstoneMaterial : Material(MapColor.RED) {
  override fun isSolid(): Boolean = true

  override fun isFluid(): Boolean = false

  override fun blocksMovement(): Boolean = true

  override fun blocksVision(): Boolean = false

  override fun suffocates(): Boolean = false

  override fun isHandHarvestable(): Boolean = true

  override fun isBurnable(): Boolean = false

  // cannot be pushed by pistons
  override fun getPistonBehavior(): Int = 2
}
