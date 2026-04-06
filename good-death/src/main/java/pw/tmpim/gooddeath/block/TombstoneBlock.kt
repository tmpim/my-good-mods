package pw.tmpim.gooddeath.block

import net.minecraft.block.Block
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.material.Material
import net.minecraft.world.World
import net.modificationstation.stationapi.api.block.BlockState
import net.modificationstation.stationapi.api.item.ItemPlacementContext
import net.modificationstation.stationapi.api.state.StateManager
import net.modificationstation.stationapi.api.state.property.BooleanProperty
import net.modificationstation.stationapi.api.state.property.DirectionProperty
import net.modificationstation.stationapi.api.template.block.TemplateBlockWithEntity
import net.modificationstation.stationapi.api.util.math.Direction
import pw.tmpim.gooddeath.GoodDeath.namespace
import java.util.Random

class TombstoneBlock: TemplateBlockWithEntity(namespace.id("tombstone"), Material.STONE) {
  companion object {
    @JvmField
    val FACING: DirectionProperty = DirectionProperty.of(
      "facing",
      Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST
    )
  }

  init {
    setHardness(COBBLESTONE.hardness)
    setResistance(6.0F)
    setSoundGroup(STONE_SOUND_GROUP)
    setTranslationKey(namespace, "tombstone")
  }

  override fun createBlockEntity(): BlockEntity = TombstoneBlockEntity(null)

  override fun isFullCube(): Boolean = false
  override fun isOpaque(): Boolean = false

  override fun appendProperties(builder: StateManager.Builder<Block?, BlockState?>?) {
    builder?.add(FACING)
    super.appendProperties(builder)
  }

  override fun getPlacementState(context: ItemPlacementContext?): BlockState? =
    defaultState?.with(FACING, context?.horizontalPlayerFacing?.opposite ?: Direction.NORTH)

  override fun randomDisplayTick(world: World?, x: Int, y: Int, z: Int, random: Random?) {
    // TODO: figure out the offsets of the candle wicks in the model and spawn flame particles there
    // world?.addParticle("flame", x + 0.5, y + 0.5, z + 0.5, 0.0, 0.0, 0.0)
  }

  override fun getPistonBehavior(): Int = 2
}
