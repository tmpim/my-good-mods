package pw.tmpim.goodfarming

import net.glasslauncher.mods.gcapi3.api.ConfigRoot
import net.mine_diver.unsafeevents.listener.EventListener
import net.minecraft.item.Item
import net.modificationstation.stationapi.api.event.mod.InitEvent
import net.modificationstation.stationapi.api.event.registry.ItemRegistryEvent
import net.modificationstation.stationapi.api.registry.ItemRegistry
import net.modificationstation.stationapi.api.tag.TagKey
import net.modificationstation.stationapi.api.util.Namespace
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import pw.tmpim.goodfarming.item.SeedBagItem

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

  @EventListener
  fun onInit(event: InitEvent) {
    log.info("$MOD_NAME initializing")
  }

  @EventListener
  fun onRegisterItems(event: ItemRegistryEvent) {
    seedBag = SeedBagItem()
  }
}
