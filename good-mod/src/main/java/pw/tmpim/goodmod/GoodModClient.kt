package pw.tmpim.goodmod

import net.fabricmc.api.ModInitializer
import net.mine_diver.unsafeevents.listener.EventListener
import net.modificationstation.stationapi.api.event.mod.InitEvent
import pw.tmpim.goodmod.GoodMod.MOD_NAME
import pw.tmpim.goodmod.GoodMod.log
import pw.tmpim.goodmod.assets.AssetFetcher
import pw.tmpim.goodmod.assets.GoodResources

object GoodModClient : ModInitializer {
  override fun onInitialize() {}

  @EventListener
  fun onInit(event: InitEvent) {
    log.info("$MOD_NAME initializing client")

    AssetFetcher.init()

    // TODO: temporary. resources should be registered in the individual thingies or something
    GoodResources.addBlock("1.12.2", "redstone_block")
    GoodResources.addBlock("1.12.2", "stonebrick")
    GoodResources.addBlock("1.12.2", "hay_block_top")
    GoodResources.addBlock("1.12.2", "hay_block_side")
    GoodResources.addBlock("1.12.2", "coal_block")
  }
}
