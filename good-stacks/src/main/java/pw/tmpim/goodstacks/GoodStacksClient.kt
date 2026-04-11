package pw.tmpim.goodstacks

import net.mine_diver.unsafeevents.listener.EventListener
import net.modificationstation.stationapi.api.client.event.gui.screen.container.TooltipBuildEvent
import net.modificationstation.stationapi.api.event.mod.InitEvent
import pw.tmpim.goodstacks.GoodStacks.MOD_NAME
import pw.tmpim.goodstacks.GoodStacks.log
import pw.tmpim.goodstacks.GoodStacks.updateItemLimits
import pw.tmpim.goodstacks.ItemCount.renderItemCountTooltip

object GoodStacksClient {
  @EventListener
  fun onInit(event: InitEvent) {
    log.info("$MOD_NAME initializing client")
  }

  @EventListener
  fun onItemTooltip(event: TooltipBuildEvent) {
    renderItemCountTooltip(event.itemStack)?.let { event.tooltip.add(it) }
  }

  @JvmStatic
  fun onGameStart() {
    // when loading a singleplayer world, reset the registry again, in case we were just on a server with a different
    // max stack size
    log.info("joining singleplayer, updating item stack sizes")
    updateItemLimits()
  }
}
