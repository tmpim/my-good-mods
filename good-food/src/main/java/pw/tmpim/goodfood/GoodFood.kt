package pw.tmpim.goodfood

import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.glasslauncher.mods.gcapi3.api.ConfigRoot
import net.mine_diver.unsafeevents.listener.EventListener
import net.minecraft.item.Item
import net.modificationstation.stationapi.api.event.mod.InitEvent
import net.modificationstation.stationapi.api.event.registry.ItemRegistryEvent
import net.modificationstation.stationapi.api.template.item.TemplateStackableFoodItem
import net.modificationstation.stationapi.api.util.Namespace
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object GoodFood : ModInitializer {
  const val MOD_ID = "good-food"
  const val MOD_NAME = "Good Food"
  val MOD_VERSION = FabricLoader.getInstance().getModContainer(MOD_ID).get().metadata.version.toString()

  val namespace: Namespace = Namespace.resolve()

  @JvmField val log: Logger = LoggerFactory.getLogger(MOD_ID)

  @JvmStatic
  @ConfigRoot(value = MOD_ID, visibleName = MOD_NAME, nameKey = "$CONFIG_KEY.name")
  val config = GoodModConfig()

  // items
  lateinit var bagelItem: Item

  override fun onInitialize() {}

  @EventListener
  fun onInit(event: InitEvent) {
    log.info("$MOD_NAME initializing")
  }

  @EventListener
  fun onRegisterItems(event: ItemRegistryEvent) {
    bagelItem = TemplateStackableFoodItem(namespace.id("bagel"), 5, false, 8)
      .setTranslationKey(namespace, "bagel")
  }
}
