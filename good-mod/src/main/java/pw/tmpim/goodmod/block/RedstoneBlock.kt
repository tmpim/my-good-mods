package pw.tmpim.goodmod.block

import net.minecraft.block.material.Material
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.modificationstation.stationapi.api.template.block.TemplateBlock
import pw.tmpim.goodmod.GoodMod.namespace

class RedstoneBlock : TemplateBlock(
  namespace.id("redstone_block"),
  /**
   * The glass material is transparent, which allows redstone behaviour in Beta 1.7.3 to work correctly for the block,
   * as World.isPoweringSide calls World.shouldSuffocate.
   *
   * The downside of this is that redstone dust can't be placed on top of the block, which RedstoneWireBlockMixin works
   * around with a `mygoodmod:redstone_dust_placeable` tag.
   */
  Material.GLASS
) {
  init {
    setTranslationKey(namespace, "redstone_block")
  }

  fun update(world: World, x: Int, y: Int, z: Int) {
    world.notifyNeighbors(x, y - 1, z, this.id)
    world.notifyNeighbors(x, y + 1, z, this.id)
    world.notifyNeighbors(x - 1, y, z, this.id)
    world.notifyNeighbors(x + 1, y, z, this.id)
    world.notifyNeighbors(x, y, z - 1, this.id)
    world.notifyNeighbors(x, y, z + 1, this.id)
  }

  override fun neighborUpdate(world: World, x: Int, y: Int, z: Int, id: Int) {
    super.neighborUpdate(world, x, y, z, id)
    world.scheduleBlockUpdate(x, y, z, id, 1)
  }

  override fun onPlaced(world: World, x: Int, y: Int, z: Int) {
    super.onPlaced(world, x, y, z)
    update(world, x, y, z)
  }

  override fun onBreak(world: World, x: Int, y: Int, z: Int) {
    super.onBreak(world, x, y, z)
    update(world, x, y, z)
  }

  override fun canEmitRedstonePower() = true

  override fun isPoweringSide(
    blockView: BlockView,
    x: Int,
    y: Int,
    z: Int,
    direction: Int
  ) = true
}
