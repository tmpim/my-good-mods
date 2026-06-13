package pw.tmpim.gooddeath.block

import net.minecraft.block.Block
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.material.Material
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.modificationstation.stationapi.api.block.BlockState
import net.modificationstation.stationapi.api.item.ItemPlacementContext
import net.modificationstation.stationapi.api.state.StateManager
import net.modificationstation.stationapi.api.state.property.BooleanProperty
import net.modificationstation.stationapi.api.state.property.DirectionProperty
import net.modificationstation.stationapi.api.template.block.TemplateBlockWithEntity
import net.modificationstation.stationapi.api.util.math.Direction
import net.modificationstation.stationapi.api.util.math.Vec3d
import pw.tmpim.gooddeath.GoodDeath
import pw.tmpim.gooddeath.GoodDeath.namespace
import pw.tmpim.gooddeath.GoodDeath.tombstoneBlock
import pw.tmpim.goodutils.block.BFS
import pw.tmpim.goodutils.block.BFS.distanceTo
import pw.tmpim.goodutils.block.BFS.toBlockPos
import pw.tmpim.goodutils.block.BFS.x
import pw.tmpim.goodutils.block.BFS.y
import pw.tmpim.goodutils.block.BFS.z
import java.util.*

class TombstoneBlock: TemplateBlockWithEntity(namespace.id("tombstone"), MATERIAL) {
  companion object {
    val FACINGS: List<Direction> = listOfNotNull(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST)
    val FACING: DirectionProperty = DirectionProperty.of(
      "facing",
      FACINGS
    )

    val FROM_DEATH: BooleanProperty = BooleanProperty.of("from_death")

    val MATERIAL: Material = TombstoneMaterial()
    const val LUMINANCE: Float = 1.0f

    val PARTICLE_POSITIONS: Array<Vec3d> = arrayOf(
      // candle1
      Vec3d(14.0, 6.0, 8.0),
      // candle2
      Vec3d(2.0, 7.0, 8.0),
      // candle3
      Vec3d(15.0, 8.0, 9.0)
    )

    val PARTICLE_POSITIONS_NORM: Array<Vec3d> =
      PARTICLE_POSITIONS.map { pos -> pos.subtract(8.0, 8.0, 8.0).multiply(1.0 / 16.0) }
        .toTypedArray()

    val PARTICLE_POSITIONS_ROT: Map<Direction, Array<Vec3d>> = FACINGS.associateWith { dir ->
      val angleY = Math.toRadians(dir.opposite.asRotation().toDouble()).toFloat()
      PARTICLE_POSITIONS_NORM.map { pos -> pos.rotateY(angleY) }.toTypedArray()
    }

    const val SPAWN_RADIUS: Int = 16

    @JvmStatic
    fun findSpawnLocation(playerEntity: PlayerEntity, radius: Int = SPAWN_RADIUS): BlockPos? {
      val world = playerEntity.world

      val deathX: Double = playerEntity.x
      val deathY: Double = playerEntity.y
      val deathZ: Double = playerEntity.z

      val sourceX = deathX.toInt()
      val sourceY = deathY.toInt()
      val sourceZ = deathZ.toInt()
      val source = BFS.Pos(sourceX, sourceY, sourceZ)

      return BFS.breadthFirstSearch(
        source,
        { pos -> pos.y >= sourceY && pos.distanceTo(source) <= radius.toDouble() },
      )
        .filter { (pos, _) -> world.isAir(pos.x, pos.y, pos.z) }
        .minByOrNull { (pos, distance) ->
          if (pos.y - 1 <= world.bottomY) {
            // Try not to spawn tombs above the void
            Int.MAX_VALUE
          } else {
            val groundState = world.getBlockState(pos.x, pos.y - 1, pos.z)
            val airScore = if (groundState.isAir) radius else 0
            val fluidScore = if (groundState.material.isFluid) radius / 2 else 0
            airScore + fluidScore + distance
          }
        }?.pos?.toBlockPos()
    }

    @JvmStatic
    fun spawnForDeadPlayer(playerEntity: PlayerEntity): Boolean {
      val world = playerEntity.world
      val spawnPos = findSpawnLocation(playerEntity)

      if (spawnPos == null) {
        playerEntity.inventory.dropInventory()
        GoodDeath.log.warn(
          "Could not spawn a tombstone for player {}, who died at ({}, {}, {})",
          playerEntity.name, playerEntity.x, playerEntity.y, playerEntity.z
        )
        return false
      }

      val facing = Direction.fromRotation(playerEntity.yaw.toDouble())
      val blockState = tombstoneBlock.defaultState
        .with(FROM_DEATH, true)
        .with(FACING, facing)

      // we must notify in case there is a block already placed there
      world.setBlockStateWithNotify(spawnPos, blockState)

      val blockEntity = world.getBlockEntity(spawnPos.x, spawnPos.y, spawnPos.z)
      GoodDeath.log.info(
        "Tombstone spawned for player {} at ({}, {}, {})",
        playerEntity.name, spawnPos.x, spawnPos.y, spawnPos.z
      )

      if (blockEntity is TombstoneBlockEntity) {
        blockEntity.bury(playerEntity)
      } else {
        GoodDeath.log.warn("Could not store player inventory in tombstone because no block entity was created?!")
      }

      return true
    }
  }

  init {
    setHardness(COBBLESTONE.hardness)
    setResistance(6.0F)
    setSoundGroup(STONE_SOUND_GROUP)
    setTranslationKey(namespace, "tombstone")
    setLuminance(LUMINANCE)
  }

  override fun createBlockEntity(): BlockEntity = TombstoneBlockEntity()

  override fun isFullCube(): Boolean = false
  override fun isOpaque(): Boolean = false

  override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
    builder.add(FACING).add(FROM_DEATH)
    super.appendProperties(builder)
  }

  override fun getPlacementState(context: ItemPlacementContext): BlockState =
    defaultState.with(FACING, context.horizontalPlayerFacing?.opposite ?: Direction.NORTH)
      .with(FROM_DEATH, false)

  override fun onBreak(world: World, x: Int, y: Int, z: Int) {
    val blockEntity = world.getBlockEntity(x, y, z)

    if (blockEntity is TombstoneBlockEntity) {
      blockEntity.dropInventory(world, x, y, z)
    }

    super.onBreak(world, x, y, z)
  }

  override fun getDroppedItemMeta(blockMeta: Int): Int = 0

  override fun getDropList(world: World, x: Int, y: Int, z: Int, state: BlockState, meta: Int): List<ItemStack>? {
    val fromDeath = state.get(FROM_DEATH)
    return if (fromDeath) emptyList() else listOf(ItemStack(this))
  }

  override fun randomDisplayTick(world: World, x: Int, y: Int, z: Int, random: Random) {
    val blockState = world.getBlockState(x, y, z)
    val facing = blockState.get(FACING)

    if (!PARTICLE_POSITIONS_ROT.contains(facing)) {
      return
    }

    for (pos in PARTICLE_POSITIONS_ROT[facing]!!) {
      world.addParticle(
        "flame",
        pos.x + x + 0.5,
        pos.y + y + 0.5,
        pos.z + z + 0.5,
        0.0,
        0.0,
        0.0
      )
    }
  }

  override fun getPistonBehavior(): Int = 2
}
