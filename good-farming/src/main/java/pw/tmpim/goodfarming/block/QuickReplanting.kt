@file:OptIn(ExperimentalAtomicApi::class)

package pw.tmpim.goodfarming.block

import net.minecraft.entity.ItemEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.modificationstation.stationapi.api.block.BlockState
import net.modificationstation.stationapi.api.util.math.Direction
import pw.tmpim.goodfarming.GoodFarming.seedBag
import pw.tmpim.goodfarming.item.SeedBagItem
import pw.tmpim.goodfarming.item.SeedBagItem.Companion.getStackSeeds
import pw.tmpim.goodfarming.world.capturingItemSpawns
import pw.tmpim.goodutils.block.matches
import pw.tmpim.goodutils.item.matches
import pw.tmpim.goodutils.item.toFirstItemStack
import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.fetchAndUpdate

object QuickReplanting {
  @JvmStatic
  fun attemptQuickReplanting(
    player: PlayerEntity?,
    world: World?,
    x: Int,
    y: Int,
    z: Int,
  ): Boolean {
    if (player == null || world == null) return false

    val state = world.getBlockState(x, y, z)
    if (state.isAir) return false
    val meta = world.getBlockMeta(x, y, z)

    // look for a registered crop type
    val (_, cropType) = CropTypeRegistry.entries
      .find { (_, type) -> type.crops.any { c -> c.matches(state, meta) }}
      ?: return false

    // we matched a crop, break it
    val capturedSeed = AtomicReference<ItemStack?>(null)
    if (cropType.seeds.isNotEmpty()) {
      // capture spawned items to test if any of them are seeds; we want to steal the first seed so we can re-plant
      world.capturingItemSpawns.set { stack ->
        cropType.seeds.find { it.matches(stack) }
          ?: return@set false // not a seed, keep spawning the item

        return@set if (stack.count > 1) {
          capturedSeed.store(stack.copy().also { it.count = 1 })
          stack.count--
          true // drop the original item entity with 1 less item
        } else {
          capturedSeed.store(stack)
          false // we consumed the only seed, prevent item spawn
        }
      }
    }

    // TODO: would be nice to partially take advantage of the interaction managers here
    val broken = world.isRemote || world.setBlock(x, y, z, 0)
    if (!broken) {
      // if we still captured an item, spawn it back
      world.capturingItemSpawns.set(null)
      val seed = capturedSeed.fetchAndUpdate { null } ?: return false
      dropStack(world, x, y, z, seed)
      return false
    }

    if (!world.isRemote) {
      state.block?.apply {
        onMetadataChange(world, x, y, z, meta)
        afterBreak(world, player, x, y, z, meta) // this is usually what spawns the crop drops
      }
    }

    world.capturingItemSpawns.set(null)

    // don't run any of the replanting behaviour on the client, we can return here now we've broken the crop
    if (world.isRemote) return true

    val plantSide = Direction.UP // TODO: should this be in the datapack?
    val plantOnPos = BlockPos(x, y, z).add(plantSide.opposite.vector)
    val plantOnBlock = world.getBlockState(plantOnPos)
    val plantOnMeta = world.getBlockMeta(plantOnPos.x, plantOnPos.y, plantOnPos.z)

    // attempt to re-plant the seed if we have one, either from the drop, or from the player's inventory
    val seed = capturedSeed.fetchAndUpdate { null }
      ?: findSeedInInventory(cropType, player, plantOnBlock, plantOnMeta)
      ?: return true // no seeds, no replant! we're done here

    if (seed.isOf(seedBag)) {
      // we didn't pick up a seed, consume a seed in the seed bag
      val bagStack = seed
      val (type, count) = getStackSeeds(bagStack) ?: return true
      val bagSeeds = type.item.toFirstItemStack(count) ?: return true

      // use the seed, not the bag, as we don't want to replant an entire radius
      if (bagSeeds.useOnBlock(player, world, plantOnPos.x, plantOnPos.y, plantOnPos.z, plantSide.id)) {
        // decrement seed bag count
        SeedBagItem.setStackSeeds(bagStack, type, count - 1)
      }
    } else {
      // plant the consumed seed directly
      seed.useOnBlock(player, world, plantOnPos.x, plantOnPos.y, plantOnPos.z, plantSide.id)
    }

    // returning true even if we didn't replant is fine, because we did break the crop at this point
    return true
  }

  private fun canUseItemAsSeed(
    cropType: CropType,
    stack: ItemStack,
    block: BlockState,
    meta: Int,
  ) = when {
    // check if the seeds in the seed bag overlap with any of the seeds we can plant
    stack.isOf(seedBag) ->
      getStackSeeds(stack)?.let { (seedType, count) ->
        // ensure the bag isn't empty:
        count > 0
        // ensure the seeds overlap:
        && cropType.seeds.any { seedType.item.matches(it) }
        // ensure we can use the seed bag to plant here:
        && (seedType.plantOnBlocks == null || seedType.plantOnBlocks.any { it.matches(block, meta) })
      } ?: false

    // look for seeds in the inventory
    else ->
      cropType.seeds.any { it.matches(stack) }
  }

  private fun findSeedInInventory(
    cropType: CropType,
    player: PlayerEntity,
    block: BlockState,
    meta: Int,
  ): ItemStack? =
    // test the selected slot first
    player.inventory.selectedItem?.takeIf { item -> canUseItemAsSeed(cropType, item, block, meta) }
      ?: player.inventory.main.asSequence()
        .filterNotNull()
        .find { item -> canUseItemAsSeed(cropType, item, block, meta) }

  private fun dropStack(world: World, x: Int, y: Int, z: Int, itemStack: ItemStack) {
    val r = 0.7f
    val vx = (world.random.nextFloat() * r).toDouble() + (1.0f - r).toDouble() * 0.5
    val vy = (world.random.nextFloat() * r).toDouble() + (1.0f - r).toDouble() * 0.5
    val vz = (world.random.nextFloat() * r).toDouble() + (1.0f - r).toDouble() * 0.5
    val entity = ItemEntity(world, x.toDouble() + vx, y.toDouble() + vy, z.toDouble() + vz, itemStack)
    entity.pickupDelay = 10
    world.spawnEntity(entity)
  }
}
