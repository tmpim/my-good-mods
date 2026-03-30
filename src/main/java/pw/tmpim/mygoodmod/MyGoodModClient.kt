package pw.tmpim.mygoodmod

import net.fabricmc.api.ModInitializer
import net.mine_diver.unsafeevents.listener.EventListener
import net.modificationstation.stationapi.api.event.mod.InitEvent
import pw.tmpim.mygoodmod.MyGoodMod.MOD_NAME
import pw.tmpim.mygoodmod.MyGoodMod.log
import pw.tmpim.mygoodmod.assets.AssetFetcher
import pw.tmpim.mygoodmod.assets.GoodResources

object MyGoodModClient : ModInitializer {
  override fun onInitialize() {}

  @EventListener
  fun onInit(event: InitEvent) {
    log.info("$MOD_NAME initializing client")

    AssetFetcher.init()

    // TODO: temporary. resources should be registered in the individual thingies or something
    GoodResources.addBlock("1.12.2", "redstone_block")
    GoodResources.addBlock("1.12.2", "stonebrick")
  }
}
