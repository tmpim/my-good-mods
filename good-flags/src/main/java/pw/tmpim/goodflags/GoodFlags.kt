package pw.tmpim.goodflags

import net.glasslauncher.mods.gcapi3.api.ConfigRoot
import net.mine_diver.unsafeevents.listener.EventListener
import net.minecraft.block.Block
import net.modificationstation.stationapi.api.event.block.entity.BlockEntityRegisterEvent
import net.modificationstation.stationapi.api.event.mod.InitEvent
import net.modificationstation.stationapi.api.event.registry.BlockRegistryEvent
import net.modificationstation.stationapi.api.util.Namespace
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import pw.tmpim.goodflags.block.FlagBlock
import pw.tmpim.goodflags.block.FlagBlockEntity
import pw.tmpim.goodflags.block.FlagPoleBlock
import pw.tmpim.goodflags.config.CONFIG_KEY
import pw.tmpim.goodflags.config.GoodFlagsConfig

object GoodFlags {
  const val MOD_ID = "good-flags"
  const val MOD_NAME = "Good Flags"

  val namespace: Namespace = Namespace.resolve()

  @JvmField val log: Logger = LoggerFactory.getLogger(MOD_ID)

  @JvmStatic
  @ConfigRoot(value = MOD_ID, visibleName = MOD_NAME, nameKey = "${CONFIG_KEY}.name")
  val config = GoodFlagsConfig()

  lateinit var flagBlock: Block
  lateinit var flagPoleBlock: Block

  @EventListener
  fun onInit(event: InitEvent) {
    log.info("$MOD_NAME initializing")
  }

  @EventListener
  fun onRegisterBlocks(event: BlockRegistryEvent) {
    log.info("$MOD_NAME registering blocks")
    flagBlock = FlagBlock()
    flagPoleBlock = FlagPoleBlock()
  }

  @EventListener
  fun onRegisterBlockEntities(event: BlockEntityRegisterEvent) {
    event.register(FlagBlockEntity::class.java, "$MOD_ID:Flag")
  }
}
