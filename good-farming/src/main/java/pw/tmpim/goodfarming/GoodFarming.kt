package pw.tmpim.goodfarming

import net.glasslauncher.mods.gcapi3.api.ConfigRoot
import net.mine_diver.unsafeevents.listener.EventListener
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.modificationstation.stationapi.api.event.mod.InitEvent
import net.modificationstation.stationapi.api.event.registry.ItemRegistryEvent
import net.modificationstation.stationapi.api.event.resource.DataResourceReloaderRegisterEvent
import net.modificationstation.stationapi.api.registry.BlockRegistry
import net.modificationstation.stationapi.api.registry.ItemRegistry
import net.modificationstation.stationapi.api.tag.TagKey
import net.modificationstation.stationapi.api.util.Namespace
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import pw.tmpim.goodfarming.item.SeedBagItem
import pw.tmpim.goodfarming.item.SeedTypeRegistry

object GoodFarming {
  const val MOD_ID = "good-farming"
  const val MOD_NAME = "Good Farming"

  val namespace: Namespace = Namespace.resolve()

  @JvmField val log: Logger = LoggerFactory.getLogger(MOD_ID)

  @JvmStatic
  @ConfigRoot(value = MOD_ID, visibleName = MOD_NAME, nameKey = "$CONFIG_KEY.name")
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
    event.resourceManager.registerReloader(SeedTypeRegistry)
  }
}
