package pw.tmpim.goodflags

import net.fabricmc.api.ModInitializer
import net.mine_diver.unsafeevents.listener.EventListener
import net.modificationstation.stationapi.api.client.event.block.entity.BlockEntityRendererRegisterEvent
import net.modificationstation.stationapi.api.event.mod.InitEvent
import pw.tmpim.goodflags.GoodFlags.MOD_NAME
import pw.tmpim.goodflags.GoodFlags.log
import pw.tmpim.goodflags.block.FlagBlockEntity
import pw.tmpim.goodflags.client.FlagBlockEntityRenderer

object GoodFlagsClient : ModInitializer {
  override fun onInitialize() {}

  @EventListener
  fun onInit(event: InitEvent) {
    log.info("$MOD_NAME initializing client")
  }

  @EventListener
  fun onRegisterBlockEntityRenderers(event: BlockEntityRendererRegisterEvent) {
    event.renderers[FlagBlockEntity::class.java] = FlagBlockEntityRenderer()
  }
}
