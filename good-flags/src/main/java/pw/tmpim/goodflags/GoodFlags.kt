package pw.tmpim.goodflags

import net.fabricmc.api.ModInitializer
import net.mine_diver.unsafeevents.listener.EventListener
import net.minecraft.block.Block
import net.modificationstation.stationapi.api.event.block.entity.BlockEntityRegisterEvent
import net.modificationstation.stationapi.api.event.mod.InitEvent
import net.modificationstation.stationapi.api.event.registry.BlockRegistryEvent
import net.modificationstation.stationapi.api.event.registry.MessageListenerRegistryEvent
import net.modificationstation.stationapi.api.util.Namespace
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import pw.tmpim.goodflags.block.FlagBlock
import pw.tmpim.goodflags.block.FlagBlockEntity
import pw.tmpim.goodflags.block.FlagPoleBlock
import pw.tmpim.goodflags.net.FlagNetworkingC2S
import pw.tmpim.goodflags.net.FlagNetworkingS2C

object GoodFlags : ModInitializer {
  const val MOD_ID = "good-flags"
  const val MOD_NAME = "Good Flags"

  val namespace: Namespace = Namespace.resolve()

  @JvmField val log: Logger = LoggerFactory.getLogger(MOD_ID)

  lateinit var flagBlock: Block
  lateinit var flagPoleBlock: Block

  override fun onInitialize() {}

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

  @EventListener
  fun onRegisterMessageListeners(event: MessageListenerRegistryEvent) {
    event.register(FlagNetworkingS2C.FLAG_SCREEN_OPEN_ID, FlagNetworkingS2C::handleFlagScreenOpen)
    event.register(FlagNetworkingS2C.FLAG_SYNC_ID, FlagNetworkingS2C::handleFlagSync)

    event.register(FlagNetworkingC2S.FLAG_UPDATE_ID, FlagNetworkingC2S::handleFlagUpdate)
  }
}
