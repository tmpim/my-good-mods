package pw.tmpim.goodflags.block

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.block.Block
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.material.Material
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.math.Box
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import net.modificationstation.stationapi.api.block.HasCustomBlockItemFactory
import net.modificationstation.stationapi.api.template.block.TemplateBlockWithEntity
import net.modificationstation.stationapi.impl.item.StationNBTSetter
import pw.tmpim.goodflags.GoodFlags
import pw.tmpim.goodflags.GoodFlags.namespace
import pw.tmpim.goodflags.item.FlagBlockItem
import pw.tmpim.goodflags.net.FlagNetworkingS2C
import pw.tmpim.goodutils.block.OnPlaceItemStack
import pw.tmpim.goodutils.net.sendToPlayer

@HasCustomBlockItemFactory(FlagBlockItem::class)
class FlagBlock : TemplateBlockWithEntity(namespace.id("flag"), Material.WOOD), OnPlaceItemStack {
  init {
    textureId = LOG.textureId
    setTranslationKey(namespace, "flag")
    setHardness(1.0F)
    setResistance(1.0F)
    setSoundGroup(WOOD_SOUND_GROUP)
    applyBoundingBox(this)
  }

  companion object {
    fun applyBoundingBox(block: Block) {
      block.setBoundingBox(
        0.5f - 0.0625f, 0.0f, 0.5f - 0.0625f,
        0.5f + 0.0625f, 1.0f, 0.5f + 0.0625f
      )
    }
  }

  override fun createBlockEntity(): BlockEntity = FlagBlockEntity()

  override fun isFullCube(): Boolean = false
  override fun isOpaque(): Boolean = false

  @Environment(EnvType.CLIENT)
  override fun getRenderType(): Int = -1

  override fun getCollisionShape(world: World, x: Int, y: Int, z: Int): Box? = null

  override fun onUse(world: World, x: Int, y: Int, z: Int, player: PlayerEntity): Boolean {
    val entity = world.getBlockEntity(x, y, z)
    if (entity is FlagBlockEntity) {
      if (!world.isRemote) {
        // request to open the screen via a packet; will short-circuit in singleplayer
        FlagNetworkingS2C.createFlagScreenOpenPacket(x, y, z).sendToPlayer(player)
      }

      return true
    }

    return false
  }

  override fun canPlaceAt(world: World, x: Int, y: Int, z: Int) =
    y + 2 < 128
      && world.getMaterial(x, y - 1, z).suffocates()
      && world.getBlockId(x, y + 1, z) == 0
      && world.getBlockId(x, y + 2, z) == 0

  override fun onPlaced(world: World, x: Int, y: Int, z: Int, placer: LivingEntity) {
    val rotation = (MathHelper.floor((placer.yaw * 4.0F / 360.0F) + 0.5) and 3)
    world.setBlockMeta(x, y, z, rotation)
    val poleId = GoodFlags.flagPoleBlock.id
    world.setBlock(x, y + 1, z, poleId)
    world.setBlock(x, y + 2, z, poleId)
  }

  override fun neighborUpdate(world: World, x: Int, y: Int, z: Int, id: Int) {
    val poleId = GoodFlags.flagPoleBlock.id
    val meta = world.getBlockMeta(x, y, z)
    var broken = false

    // The pole at y+1 must exist — if it was mined away the structure is invalid
    if (world.getBlockId(x, y + 1, z) != poleId) {
      broken = true
      // Silently remove the top pole too if it's still there
      if (world.getBlockId(x, y + 2, z) == poleId) {
        world.setBlock(x, y + 2, z, 0)
      }
    }

    // The floor below must be solid
    if (!world.getMaterial(x, y - 1, z).suffocates()) {
      broken = true
      // Silently remove both pole blocks above
      if (world.getBlockId(x, y + 1, z) == poleId) {
        world.setBlock(x, y + 1, z, 0)
      }
      if (world.getBlockId(x, y + 2, z) == poleId) {
        world.setBlock(x, y + 2, z, 0)
      }
    }

    // Double check that we still exist before dropping a stack.
    if (broken && world.getBlockId(x, y, z) == this.id) {
      if (!world.isRemote) {
        dropStacks(world, x, y, z, meta)
      }
      world.setBlock(x, y, z, 0)
    }
  }

  override fun dropStacks(world: World, x: Int, y: Int, z: Int, meta: Int, luck: Float) {
    if (world.isRemote) return
    val entity = world.getBlockEntity(x, y, z)
    val stack = ItemStack(this)
    if (entity is FlagBlockEntity && entity.isPainted) {
      val nbt = NbtCompound()
      nbt.putByteArray("Pixels", entity.pixels)
      StationNBTSetter.cast(stack).setStationNbt(nbt)
    }
    dropStack(world, x, y, z, stack)
  }

  override fun getPistonBehavior() = 2 // unpushable

  override fun onPlaced(
    world: World,
    x: Int,
    y: Int,
    z: Int,
    itemStack: ItemStack
  ) {
    val stationNbt = itemStack.stationNbt
    if (stationNbt == null || !stationNbt.contains("Pixels")) return

    val entity = world.getBlockEntity(x, y, z)
    if (entity is FlagBlockEntity) {
      val pixels = stationNbt.getByteArray("Pixels")
      entity.setAllPixels(pixels)
    }
  }
}
