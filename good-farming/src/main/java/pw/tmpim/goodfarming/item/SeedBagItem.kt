package pw.tmpim.goodfarming.item

import net.minecraft.client.resource.language.I18n
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.hit.HitResultType
import net.minecraft.util.hit.HitResultType.BLOCK
import net.minecraft.world.World
import net.modificationstation.stationapi.api.client.item.CustomTooltipProvider
import net.modificationstation.stationapi.api.item.CustomReachProvider
import net.modificationstation.stationapi.api.template.item.TemplateItem
import net.modificationstation.stationapi.api.util.math.Direction
import pw.tmpim.goodfarming.GoodFarming
import pw.tmpim.goodfarming.GoodFarming.MOD_ID
import pw.tmpim.goodfarming.GoodFarming.namespace
import pw.tmpim.goodutils.getItemSubKey
import pw.tmpim.goodutils.removeTag
import kotlin.math.max

class SeedBagItem : TemplateItem(namespace.id("seed_bag")), CustomReachProvider, CustomTooltipProvider {
  init {
    setTranslationKey(namespace, "seed_bag")
  }

  override fun useOnBlock(
    stack: ItemStack,
    user: PlayerEntity,
    world: World,
    x: Int,
    y: Int,
    z: Int,
    side: Int
  ): Boolean {
    // only allow use on top of block
    if (side != Direction.UP.id || world.isRemote) return false

    val seeds = getStackSeeds(stack) ?: return false
    val item = seeds.item
    var remaining = seeds.count

    // try to plant seeds within the range, Y-major
    val lateralRadius = config.seedBagPlantLateralRadius ?: 3
    val verticalRadius = config.seedBagPlantVerticalRadius ?: 2

    spiral3D(x, y, z, lateralRadius, verticalRadius, world.bottomY, world.topY)
      .takeWhile { remaining > 0 }
      .forEach { (x, y, z) ->
        // use the seed's own useOnBlock function
        if (item.useOnBlock(seeds, user, world, x, y, z, side)) {
          remaining--
        }
      }

    seeds.count = remaining
    setStackSeeds(stack, seeds)

    return true
  }

  override fun getReach(
    stack: ItemStack,
    player: PlayerEntity,
    type: HitResultType,
    currentReach: Double
  ): Double = when (type) {
    BLOCK -> config.seedBagThrowRange ?: 5.0
    else -> currentReach
  }

  override fun getMaxCount() = 1
  override fun getMaxDamage() = config.seedBagCapacity ?: 512

  override fun getTooltip(
    stack: ItemStack,
    originalTooltip: String
  ): Array<out String> {
    val seeds = getStackSeeds(stack) ?: return arrayOf(originalTooltip)

    return arrayOf(
      originalTooltip,
      "§7" + I18n.getTranslation(
        getItemSubKey(this, "tooltip.seeds"),
        seeds.count,
        I18n.getTranslation(seeds.item.getTranslationKey(seeds) + ".name")
      ),
    )
  }

  override fun getTextureId(stack: ItemStack): Int {
    // TODO: a purely data-driven way to do this would be nice
    val seeds = getStackSeeds(stack) ?: return SeedBagItemTextures.base.index

    return when {
      seeds.isOf(SEEDS) -> SeedBagItemTextures.wheatSeeds
      seeds.isOf(DYE) && seeds.damage == 15 -> SeedBagItemTextures.boneMeal
      else -> SeedBagItemTextures.base
    }.index
  }

  companion object {
    const val SEEDS_KEY = "$MOD_ID:seeds"
    const val SEEDS_COUNT_KEY = "$MOD_ID:seeds_count" // serialised separately, since ItemStack count is limited to 127

    private val config by GoodFarming::config

    // ideally the tag should be sufficient, but sadly they can't contain damage values... a purely data-driven way
    // to do this would be nice!!
    fun isSeedValid(stack: ItemStack) =
      stack.isIn(GoodFarming.seeds) && (!stack.isOf(DYE) || stack.damage == 15 /* bone meal */)

    fun getStackSeeds(bagStack: ItemStack?): ItemStack? {
      if (bagStack == null) return null

      val nbt = bagStack.stationNbt.takeIf { it.contains(SEEDS_KEY) } ?: return null
      val count = nbt.getInt(SEEDS_COUNT_KEY).takeIf { it > 0 } ?: return null
      val seeds = nbt.getCompound(SEEDS_KEY)

      return ItemStack(seeds)
        .takeIf { it.count > 0 && isSeedValid(it) }
        ?.also { it.count = count }
    }

    fun setStackSeeds(bagStack: ItemStack, seedStack: ItemStack?) {
      val count = seedStack?.count ?: 0

      if (seedStack == null || count <= 0) {
        // exhaust the bag
        bagStack.damage = 0
        bagStack.stationNbt.removeTag(SEEDS_KEY)
        bagStack.stationNbt.putInt(SEEDS_COUNT_KEY, 0)
      } else {
        // ItemStack count is limited to 127; avoid overflowing byte and store count separately
        val newSeedStack = seedStack.copy().also { it.count = 1 }

        // set the stack damage, without affecting the bag's count (i.e. don't use damage() here)
        bagStack.damage = max(0, bagStack.maxDamage - count)
        bagStack.stationNbt.put(SEEDS_KEY, NbtCompound().apply { newSeedStack.writeNbt(this) })
        bagStack.stationNbt.putInt(SEEDS_COUNT_KEY, count)
      }
    }

    private fun spiral3D(
      cx: Int,
      cy: Int,
      cz: Int,
      rX: Int,
      rY: Int,
      minY: Int,
      maxY: Int,
    ): Sequence<Triple<Int, Int, Int>> = sequence {
      val yOffsets = sequence { // 0, +1, -1, +2, -2, ...
        yield(0)
        for (r in 1..rY) {
          yield(r)
          yield(-r)
        }
      }

      for (dy in yOffsets) {
        if (cy + dy !in minY..<maxY) {
          continue // skip the y layer if it's out of the world
        }

        // shell 0: center column
        yield(Triple(cx, cy + dy, cz))

        // shell r: walk the square perimeter
        for (r in 1..rX) {
          var x = -r
          var z = -r

          while (x < r)  { yield(Triple(cx + x, cy + dy, cz + z)); x++ }
          while (z < r)  { yield(Triple(cx + x, cy + dy, cz + z)); z++ }
          while (x > -r) { yield(Triple(cx + x, cy + dy, cz + z)); x-- }
          while (z > -r) { yield(Triple(cx + x, cy + dy, cz + z)); z-- }
        }
      }
    }
  }
}
