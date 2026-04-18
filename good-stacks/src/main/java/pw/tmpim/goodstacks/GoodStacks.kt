package pw.tmpim.goodstacks

import net.glasslauncher.mods.gcapi3.api.ConfigRoot
import net.glasslauncher.mods.gcapi3.api.PostConfigLoadedListener
import net.glasslauncher.mods.gcapi3.api.PreConfigSavedListener
import net.glasslauncher.mods.gcapi3.impl.EventStorage.EventSource.*
import net.glasslauncher.mods.gcapi3.impl.GlassYamlFile
import net.mine_diver.unsafeevents.listener.EventListener
import net.mine_diver.unsafeevents.listener.ListenerPriority
import net.modificationstation.stationapi.api.event.mod.InitEvent
import net.modificationstation.stationapi.api.event.registry.ItemRegistryEvent
import net.modificationstation.stationapi.api.registry.ItemRegistry
import net.modificationstation.stationapi.api.util.Namespace
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import pw.tmpim.goodstacks.config.CONFIG_KEY
import pw.tmpim.goodstacks.config.GoodStacksConfig
import java.util.*
import kotlin.jvm.optionals.getOrNull
import kotlin.math.min

object GoodStacks : PostConfigLoadedListener, PreConfigSavedListener {
  const val MOD_ID = "good-stacks"
  const val MOD_NAME = "Good Stacks"

  val namespace: Namespace = Namespace.resolve()

  @JvmField val log: Logger = LoggerFactory.getLogger(MOD_ID)

  @JvmStatic
  @ConfigRoot(value = MOD_ID, visibleName = MOD_NAME, nameKey = "${CONFIG_KEY}.name")
  val config = GoodStacksConfig()

  private val debug
    get() = config.debug == true

  @EventListener
  fun onInit(event: InitEvent) {
    log.info("$MOD_NAME initializing")
  }

  @JvmStatic
  fun getMaxStack(): Optional<Int>
    = Optional.ofNullable(config.maxStackSize?.takeUnless { it == 0 || it == 64 })

  /** updates the limits for every registered item if they were the default 64 */
  fun updateItemLimits(vanilla: Boolean = false) {
    val configMax = getMaxStack().getOrNull()

    ItemRegistry.INSTANCE.streamEntries().forEach { entry ->
      val item = entry.value()

      val originalMax = item.`goodstacks$originalMaxCount`
      val newMax = configMax?.takeIf { !vanilla && it > 0 && originalMax == 64 }
        ?: originalMax
        ?: 64.also { log.warn("max stack size for $item was set to 64 as we don't know what it originally was!") }

      if (debug) log.debug("{} {} {} -> {} (original: {})", entry.key, item, item.maxCount, newMax, originalMax)
      item.`goodstacks$setMaxCount`(newMax)
    }
  }

  @EventListener(priority = ListenerPriority.LOWEST)
  fun onItemRegister(event: ItemRegistryEvent) {
    // the mixins to Item.<init> and Item.setMaxCount capture the originalMaxCount for basic items, but not for anything
    // that extends the Item class and sets the protected maxCount field in its constructor, or override getMaxCount...
    // so let's try to capture those too
    log.debug("in onItemRegister")

    ItemRegistry.INSTANCE.streamEntries().forEach { entry ->
      val item = entry.value()

      if (item.maxCount == 0) {
        if (debug) log.debug("register {} {} original max is 0, skipping", entry.key, item)
      } else {
        if (debug) log.debug(
          "register {} {} current original: {} maxCount: {}",
          entry.key, item, item.`goodstacks$originalMaxCount`, item.maxCount
        )
        item.`goodstacks$originalMaxCount` = min(item.`goodstacks$originalMaxCount` ?: 64, item.maxCount)
      }
    }
  }

  override fun PostConfigLoaded(source: Int) {
    when {
      containsOne(source, VANILLA_SERVER_JOIN) -> {
        log.info("joining vanilla server, resetting item stack sizes to vanilla")
        updateItemLimits(vanilla = true)
      }

      containsOne(source, MODDED_SERVER_JOIN) -> {
        log.info("joining modded server, resetting item stack sizes to server's configuration")
        // the config is supposed to be synced at this point, and it is, but our own config object hasn't been mutated
        // for some unknown reason (bug in gcapi?) anyway lets try reloading it...
        // GCAPI.reloadConfig("$MOD_ID:$MOD_ID") // TODO: still doesn't work
        log.info("new item limit: ${config.maxStackSize}")
        updateItemLimits()
      }
    }
  }

  override fun onPreConfigSaved(source: Int, oldValues: GlassYamlFile?, newValues: GlassYamlFile?) {
    when {
      containsOne(source, USER_SAVE) || containsOne(source, MOD_SAVE) -> {
        log.info("config saved, updating item stack sizes")
        updateItemLimits()
      }
    }
  }
}
