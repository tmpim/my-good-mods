package pw.tmpim.mygoodmod.block

import net.minecraft.block.material.Material
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.modificationstation.stationapi.api.template.block.TemplateBlock
import pw.tmpim.mygoodmod.MyGoodMod.namespace

class RedstoneBlock : TemplateBlock(
  namespace.id("redstone_block"),
  Material.METAL
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

  override fun isEmittingRedstonePowerInDirection(
    blockView: BlockView,
    x: Int,
    y: Int,
    z: Int,
    direction: Int
  ) = true

  override fun canTransferPowerInDirection(
    world: World,
    x: Int,
    y: Int,
    z: Int,
    direction: Int
  ) = true
}
