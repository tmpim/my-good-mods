package pw.tmpim.goodmod

import net.fabricmc.api.ModInitializer
import net.mine_diver.unsafeevents.listener.EventListener
import net.modificationstation.stationapi.api.event.mod.InitEvent
import pw.tmpim.goodassetfetcher.api.GoodAssetFetcherRegistryEvent
import pw.tmpim.goodmod.GoodMod.MOD_NAME
import pw.tmpim.goodmod.GoodMod.log
import pw.tmpim.goodmod.GoodMod.namespace

object GoodModClient : ModInitializer {
  override fun onInitialize() {}

  @EventListener
  fun onInit(event: InitEvent) {
    log.info("$MOD_NAME initializing client")
  }

  @EventListener
  fun onRegisterGoodAssets(event: GoodAssetFetcherRegistryEvent) {
    event.addBlock(namespace, "1.12.2", "redstone_block")
    event.addBlock(namespace, "1.12.2", "stonebrick")
    event.addBlock(namespace, "1.12.2", "hay_block_top")
    event.addBlock(namespace, "1.12.2", "hay_block_side")
    event.addBlock(namespace, "1.12.2", "coal_block")
  }
}
