package pw.tmpim.goodsounds

import net.fabricmc.api.ModInitializer
import net.glasslauncher.mods.gcapi3.api.ConfigRoot
import net.mine_diver.unsafeevents.listener.EventListener
import net.minecraft.item.Item
import net.modificationstation.stationapi.api.event.mod.InitEvent
import net.modificationstation.stationapi.api.registry.ItemRegistry
import net.modificationstation.stationapi.api.tag.TagKey
import net.modificationstation.stationapi.api.util.Namespace
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object GoodSounds : ModInitializer {
  const val MOD_ID = "good-sounds"
  const val MOD_NAME = "Good Sounds"

  val namespace: Namespace = Namespace.resolve()
  @JvmField val log: Logger = LoggerFactory.getLogger(MOD_ID)

  @JvmField val metalItem: TagKey<Item> = TagKey.of(ItemRegistry.KEY, namespace.id("metal_item"))

  @JvmStatic
  @ConfigRoot(value = MOD_ID, visibleName = MOD_NAME, nameKey = "$CONFIG_KEY.name")
  val config = GoodSoundsConfig()

  const val DEFAULT_RAIN_VOLUME: Float = 0.25f
  const val DEFAULT_METAL_PIPE_VOLUME: Float = 1.0f

  const val SOUND_METAL_PIPE = "$MOD_ID:metal_pipe"

  override fun onInitialize() {}

  @JvmStatic
  fun isMetalItem(item: Item?): Boolean =
    if (item == null)
      false
    else
      ItemRegistry.INSTANCE.getEntry(item).getTags().contains(metalItem)

  @EventListener
  fun onInit(event: InitEvent) {
    log.info("$MOD_NAME initializing")
  }
}
