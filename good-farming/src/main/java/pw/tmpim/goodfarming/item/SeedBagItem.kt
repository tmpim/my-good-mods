package pw.tmpim.goodfarming.item

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.glasslauncher.mods.alwaysmoreitems.api.SubItemProvider
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.render.item.ItemRenderer
import net.minecraft.client.texture.TextureManager
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.hit.HitResultType
import net.minecraft.util.hit.HitResultType.BLOCK
import net.minecraft.world.World
import net.modificationstation.stationapi.api.client.item.CustomItemOverlay
import net.modificationstation.stationapi.api.client.item.CustomTooltipProvider
import net.modificationstation.stationapi.api.item.CustomReachProvider
import net.modificationstation.stationapi.api.template.item.TemplateItem
import net.modificationstation.stationapi.api.util.math.Direction
import net.modificationstation.stationapi.impl.item.StationNBTSetter
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.GL_DEPTH_TEST
import org.lwjgl.opengl.GL11.GL_LIGHTING
import pw.tmpim.goodfarming.GoodFarming
import pw.tmpim.goodfarming.GoodFarming.MOD_ID
import pw.tmpim.goodfarming.GoodFarming.namespace
import pw.tmpim.goodutils.block.matches
import pw.tmpim.goodutils.i18n.getItemSubKey
import pw.tmpim.goodutils.i18n.i18n
import pw.tmpim.goodutils.item.toFirstItemStack
import pw.tmpim.goodutils.nbt.getId
import pw.tmpim.goodutils.nbt.putId
import pw.tmpim.goodutils.nbt.remove
import pw.tmpim.goodutils.world.spiral3D
import kotlin.math.ceil
import kotlin.math.max

class SeedBagItem :
  TemplateItem(namespace.id("seed_bag")),
  CustomReachProvider,
  CustomTooltipProvider,
  CustomItemOverlay
{
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

    val (type, count) = getStackSeeds(stack) ?: return false
    var remaining = count
    val seeds = type.item.toFirstItemStack(remaining) ?: return false

    // try to plant seeds within the range, Y-major
    val lateralRadius = config.seedBagPlantLateralRadius ?: 3
    val verticalRadius = config.seedBagPlantVerticalRadius ?: 2

    world.spiral3D(x, y, z, lateralRadius, verticalRadius)
      .takeWhile { remaining > 0 }
      .forEach { (x, y, z) ->
        val block = world.getBlockState(x, y, z)
        if (block.isAir) return@forEach
        val meta = world.getBlockMeta(x, y, z)

        if (
          // check we can use the seeds on this block
          (type.plantOnBlocks == null || type.plantOnBlocks.any { it.matches(block, meta) })
          // use the seed's own useOnBlock function
          && seeds.useOnBlock(user, world, x, y, z, side)
        ) {
          remaining--
        }
      }

    // save the count back to the bag's nbt
    setStackSeeds(stack, type, remaining)

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
    val (type, count) = getStackSeeds(stack) ?: return arrayOf(originalTooltip)
    val item = type.cachedFirstItem
    val itemName = item?.translationKey?.let { "$it.name".i18n() }
      ?: getItemSubKey(this, "tooltip.unknown").i18n()

    return arrayOf(
      originalTooltip,
      "§7" + getItemSubKey(this, "tooltip.seeds").i18n(count, itemName),
    )
  }

  @Environment(EnvType.CLIENT)
  override fun getTextureId(stack: ItemStack): Int {
    val (type) = getStackSeeds(stack) ?: return SeedBagTextureRegistry.baseTexture.index
    return SeedBagTextureRegistry.getBagTexture(type).index
  }

  @Environment(EnvType.CLIENT)
  override fun renderItemOverlay(
    item: ItemRenderer,
    itemX: Int,
    itemY: Int,
    stack: ItemStack,
    tr: TextRenderer,
    tm: TextureManager
  ) {
    if (GoodFarming.config.seedBagOverlayEnabled != true) return

    val (_, count) = getStackSeeds(stack) ?: return
    val color = if (maxDamage >= 64 && count <= ceil(maxDamage / 16.0)) {
      0xFFDD00
    } else {
      0xFFFFFF
    }

    GL11.glDisable(GL_LIGHTING)
    GL11.glDisable(GL_DEPTH_TEST)

    tr.drawWithShadow(formatSeedCount(count), itemX, itemY, color)

    GL11.glEnable(GL_LIGHTING)
    GL11.glEnable(GL_DEPTH_TEST)
  }

  // AMI support for our available seed bags
  @SubItemProvider
  fun getSubItems(): List<ItemStack> =
    listOf(ItemStack(this)) +
      SeedTypeRegistry.entries.map { (_, type) ->
        ItemStack(this).also { setStackSeeds(it, type, maxDamage) }
      }

  companion object {
    const val SEED_TYPE_KEY = "$MOD_ID:seed_type"
    const val SEED_COUNT_KEY = "$MOD_ID:seed_count"

    private val config by GoodFarming::config

    fun getStackSeeds(bagStack: ItemStack?): Pair<SeedType, Int>? {
      if (bagStack == null) return null

      val nbt = bagStack.stationNbt.takeIf { it.contains(SEED_TYPE_KEY) } ?: return null

      val count = nbt.getInt(SEED_COUNT_KEY).takeIf { it > 0 } ?: return null
      val typeId = nbt.getId(SEED_TYPE_KEY) ?: return null
      val type = SeedTypeRegistry.get(typeId) ?: return null

      return type to count
    }

    fun setStackSeeds(bagStack: ItemStack, seeds: SeedType?, count: Int) {
      val nbt = bagStack.stationNbt.copy() // TODO: remove this copy when AMI singleplayer cheat bug is fixed

      if (seeds == null || count <= 0) {
        // exhaust the bag
        bagStack.damage = 0
        nbt.remove(SEED_TYPE_KEY)
        nbt.remove(SEED_COUNT_KEY)
      } else {
        // set the stack damage, without affecting the bag's count (i.e. don't use damage() here)
        bagStack.damage = max(0, bagStack.maxDamage - count)
        nbt.putId(SEED_TYPE_KEY, seeds.id)
        nbt.putInt(SEED_COUNT_KEY, count)
      }

      // TODO: remove this when AMI singleplayer cheat bug is fixed
      // this is an internal API https://discord.com/channels/832632993080279070/1122814637776322650/1333596819363725342
      StationNBTSetter.cast(bagStack).setStationNbt(nbt)
    }

    fun formatSeedCount(n: Int): String = when {
      n < 1_000         -> "$n"
      n < 10_000        -> "%.1fK".format(n / 1_000.0)
      n < 1_000_000     -> "${n / 1_000}K"
      n < 1_000_000_000 -> "%.1fM".format(n / 1_000_000.0)
      else              -> "%.1fB".format(n / 1_000_000_000.0)
    }
  }
}
