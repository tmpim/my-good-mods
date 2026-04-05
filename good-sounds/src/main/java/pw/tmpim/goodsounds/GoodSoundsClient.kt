package pw.tmpim.goodsounds

import net.fabricmc.api.ModInitializer
import net.mine_diver.unsafeevents.listener.EventListener
import net.minecraft.block.PressurePlateBlock
import net.minecraft.util.math.BlockPos
import net.modificationstation.stationapi.api.event.mod.InitEvent
import net.modificationstation.stationapi.api.event.world.BlockSetEvent
import pw.tmpim.goodsounds.GoodSounds.MOD_NAME
import pw.tmpim.goodsounds.GoodSounds.log

object GoodSoundsClient : ModInitializer {
  private val platePressed = mutableSetOf<BlockPos>()

  override fun onInitialize() {}

  @EventListener
  fun onInit(event: InitEvent) {
    log.info("$MOD_NAME initializing client")
  }

  @EventListener
  fun onBlockSet(event: BlockSetEvent) {
    val world = event.world

    if (!world.isRemote) {
      return
    }

    val block = event.blockState.block
    val meta = event.blockMeta

    if (block is PressurePlateBlock) {
      val pos = BlockPos(event.x, event.y, event.z)

      if (meta == 1) {
        // Pressure plate was just pressed, write that down
        platePressed.add(pos)
      } else if (meta == 0) {
        if (platePressed.contains(pos)) {
          // Pressure plate was just released, play the click
          world.playSound(event.x + 0.5, event.y + 0.1, event.z + 0.5, "random.click", 0.3f, 0.5f)
        }
      }
    }
  }
}
