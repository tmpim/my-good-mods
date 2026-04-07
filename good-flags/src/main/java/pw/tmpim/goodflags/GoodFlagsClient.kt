package pw.tmpim.goodflags

import net.mine_diver.unsafeevents.listener.EventListener
import net.modificationstation.stationapi.api.client.event.block.entity.BlockEntityRendererRegisterEvent
import net.modificationstation.stationapi.api.client.event.resource.AssetsReloadEvent
import net.modificationstation.stationapi.api.event.mod.InitEvent
import pw.tmpim.goodflags.GoodFlags.MOD_NAME
import pw.tmpim.goodflags.GoodFlags.log
import pw.tmpim.goodflags.block.FlagBlockEntity
import pw.tmpim.goodflags.client.FlagBlockEntityRenderer

object GoodFlagsClient {
  @EventListener
  fun onInit(event: InitEvent) {
    log.info("$MOD_NAME initializing client")
  }

  @EventListener
  fun onRegisterBlockEntityRenderers(event: BlockEntityRendererRegisterEvent) {
    event.renderers[FlagBlockEntity::class.java] = FlagBlockEntityRenderer()
  }

  @EventListener
  fun onAssetsReloaded(event: AssetsReloadEvent) {
    FlagBlockEntityRenderer.clearTextureCache() // TODO: check this actually runs on the render thread
  }
}
