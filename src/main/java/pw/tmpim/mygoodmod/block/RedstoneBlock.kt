package pw.tmpim.mygoodmod.block

import net.minecraft.block.material.Material
import net.minecraft.world.BlockView
import net.modificationstation.stationapi.api.template.block.TemplateBlock
import pw.tmpim.mygoodmod.MyGoodMod.namespace

class RedstoneBlock : TemplateBlock(
  namespace.id("redstone_block"),
  Material.METAL
) {
  init {
    setTranslationKey(namespace, "redstone_block")
  }

  override fun canEmitRedstonePower() = true

  override fun isEmittingRedstonePowerInDirection(
    blockView: BlockView,
    x: Int,
    y: Int,
    z: Int,
    direction: Int
  ) = true
}
