package pw.tmpim.goodfarming

import net.glasslauncher.mods.gcapi3.api.ConfigRoot
import net.glasslauncher.mods.gcapi3.api.PostConfigLoadedListener
import net.glasslauncher.mods.gcapi3.api.PreConfigSavedListener
import net.glasslauncher.mods.gcapi3.impl.EventStorage.EventSource.*
import net.glasslauncher.mods.gcapi3.impl.GlassYamlFile
import net.glasslauncher.mods.networking.GlassNetworking
import net.mine_diver.unsafeevents.listener.EventListener
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.modificationstation.stationapi.api.event.mod.InitEvent
import net.modificationstation.stationapi.api.event.registry.ItemRegistryEvent
import net.modificationstation.stationapi.api.event.resource.DataReloadEvent
import net.modificationstation.stationapi.api.event.resource.DataResourceReloaderRegisterEvent
import net.modificationstation.stationapi.api.registry.BlockRegistry
import net.modificationstation.stationapi.api.registry.ItemRegistry
import net.modificationstation.stationapi.api.tag.TagKey
import net.modificationstation.stationapi.api.util.Namespace
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import pw.tmpim.goodfarming.block.CropTypeRegistry
import pw.tmpim.goodfarming.config.CONFIG_KEY
import pw.tmpim.goodfarming.config.GoodFarmingConfig
import pw.tmpim.goodfarming.item.SeedBagItem
import pw.tmpim.goodfarming.item.SeedTypeRegistry
import pw.tmpim.goodfarming.net.GoodFarmingNetworkingC2S.sendPlayerConfiguration
import pw.tmpim.goodutils.misc.isClient

object GoodFarming : PostConfigLoadedListener, PreConfigSavedListener {
  const val MOD_ID = "good-farming"
  const val MOD_NAME = "Good Farming"

  val namespace: Namespace = Namespace.resolve()

  @JvmField val log: Logger = LoggerFactory.getLogger(MOD_ID)

  @JvmStatic
  @ConfigRoot(value = MOD_ID, visibleName = MOD_NAME, nameKey = "${CONFIG_KEY}.name")
  val config = GoodFarmingConfig()

  // items
  lateinit var seedBag: SeedBagItem

  // tags
  val seeds: TagKey<Item> = TagKey.of(ItemRegistry.KEY, namespace.id("seeds"))
  val saplings: TagKey<Block> = TagKey.of(BlockRegistry.KEY, namespace.id("saplings"))
  val crops: TagKey<Block> = TagKey.of(BlockRegistry.KEY, namespace.id("crops"))

  @EventListener
  fun onInit(event: InitEvent) {
    log.info("$MOD_NAME initializing")
  }

  @EventListener
  fun onRegisterItems(event: ItemRegistryEvent) {
    seedBag = SeedBagItem()
  }

  @EventListener
  fun onRegisterResourceReloaders(event: DataResourceReloaderRegisterEvent) {
    event.resourceManager.registerReloader(CropTypeRegistry)
    event.resourceManager.registerReloader(SeedTypeRegistry)
  }

  @EventListener
  fun onDataReload(event: DataReloadEvent) {
    SeedTypeRegistry.entries.onEach { (_, value) -> value.lazyFirstItem.clear() }
  }

  override fun PostConfigLoaded(source: Int) {
    // whenever the client's config is ready, send it over to the server (mainly just during join for this event)
    if (
      isClient
      && containsOne(source, MODDED_SERVER_JOIN)
      && GlassNetworking.serverHasNetworking()
    ) {
      log.debug("sending configuration to server (PostConfigLoaded)")
      sendPlayerConfiguration()
    }
  }

  override fun onPreConfigSaved(source: Int, oldValues: GlassYamlFile, newValues: GlassYamlFile) {
    // the new values should be populated, so send them to the server
    if (
      isClient
      && (containsOne(source, USER_SAVE) || containsOne(source, MOD_SAVE))
      && GlassNetworking.serverHasNetworking()
    ) {
      log.debug("sending configuration to server (onPreConfigSaved)")
      sendPlayerConfiguration()
    }
  }
}
