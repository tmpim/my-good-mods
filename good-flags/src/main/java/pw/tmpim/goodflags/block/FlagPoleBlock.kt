package pw.tmpim.goodflags.block

import net.minecraft.block.material.Material
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.Box
import net.minecraft.world.World
import net.modificationstation.stationapi.api.template.block.TemplateBlock
import pw.tmpim.goodflags.GoodFlags
import pw.tmpim.goodflags.GoodFlags.namespace
import java.util.*

class FlagPoleBlock : TemplateBlock(namespace.id("flagpole"), Material.WOOD) {
  init {
    setTranslationKey(namespace, "flagpole")
    FlagBlock.applyBoundingBox(this)
  }

  override fun getRenderType() = -1
  override fun isFullCube(): Boolean = false
  override fun isOpaque(): Boolean = false

  // Never drop an item — only the base FlagBlock drops the item
  override fun getDroppedItemId(blockMeta: Int, random: Random): Int = 0

  override fun getCollisionShape(world: World, x: Int, y: Int, z: Int): Box? = null

  /**
   * Delegate use interactions up/down to the base FlagBlock so the player can
   * open the paint screen by clicking any part of the flag structure.
   */
  override fun onUse(world: World, x: Int, y: Int, z: Int, player: PlayerEntity): Boolean {
    val baseY = findBaseY(world, x, y, z) ?: return false
    return GoodFlags.flagBlock.onUse(world, x, baseY, z, player)
  }

  override fun neighborUpdate(world: World, x: Int, y: Int, z: Int, id: Int) {
    val flagId = GoodFlags.flagBlock.id
    val poleId = this.id
    val below = world.getBlockId(x, y - 1, z)
    val above = world.getBlockId(x, y + 1, z)

    // If the block below is neither the base nor another pole segment, this
    // pole has lost its support. Remove self silently — no drop.
    // The upper pole (if any) will receive its own neighborUpdate and do the same.
    if ((below != flagId && below != poleId) || (below == flagId && above != poleId)) {
      world.setBlock(x, y, z, 0)
    }
  }

  /**
   * Walks downward from (x, y, z) through FlagPoleBlock layers until it finds
   * the FlagBlock base. Returns null if no base is found within the expected range.
   */
  private fun findBaseY(world: World, x: Int, y: Int, z: Int): Int? {
    val flagId = GoodFlags.flagBlock.id
    val poleId = this.id
    var currentY = y - 1
    while (currentY >= 0) {
      val blockId = world.getBlockId(x, currentY, z)
      if (blockId == flagId) return currentY
      if (blockId != poleId) break
      currentY--
    }
    return null
  }

  override fun getPistonBehavior() = 2
}
