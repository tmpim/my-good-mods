package pw.tmpim.goodboatfix

import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.glasslauncher.mods.gcapi3.api.ConfigRoot
import net.mine_diver.unsafeevents.listener.EventListener
import net.modificationstation.stationapi.api.event.mod.InitEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object GoodBoatFix : ModInitializer {
  const val MOD_ID = "good-boat-fix"
  const val MOD_NAME = "Good Boat Fix"

  @JvmField val log: Logger = LoggerFactory.getLogger(MOD_ID)

  @JvmStatic
  @ConfigRoot(value = MOD_ID, visibleName = MOD_NAME, nameKey = "$CONFIG_KEY.name")
  val config = GoodBoatFixConfig()

  private var unitweaksInstalled: Boolean = false

  override fun onInitialize() {}

  @EventListener
  fun onInit(event: InitEvent) {
    log.info("$MOD_NAME initializing")

    // If UniTweaks is installed, attempt to check if their boat mixin is enabled
    var unitweaksException: Throwable? = null
    try {
      unitweaksInstalled = FabricLoader.getInstance().getModContainer("unitweaks").isPresent

      if (unitweaksInstalled && config.boatsDropBoatItem == true) {
        // don't use try here; we want to bubble the error
        if (GoodBoatUniTweaksCheck.checkUniTweaksBoatMixinEnabled()) {
          log.error("UniTweaks boatsDropThemselves is incompatible with good-boat-fix. Disable one or the other. " +
            "good-boat-fix will automatically disable itself until either tweak is disabled.")
        }
      }
    } catch (e: Exception) {
      unitweaksException = e
    } catch (e: LinkageError) { // These are not exceptions so must be handled separately
      unitweaksException = e
    } catch (e: VirtualMachineError) {
      unitweaksException = e
    }

    if (unitweaksException != null) {
      log.error("Failed to check for compatibility with UniTweaks; boat fix may misbehave or result in duplicate" +
        " boats. Configure both mods appropriately.", unitweaksException)
    }
  }

  private fun tryCheckUniTweaksBoatMixinEnabled() = try {
    GoodBoatUniTweaksCheck.checkUniTweaksBoatMixinEnabled()
  } catch (_: Exception) {
    false
  } catch (_: LinkageError) {
    false
  } catch (_: VirtualMachineError) {
    false
  }

  @JvmStatic
  fun shouldApplyMixin() =
    (config.boatsDropBoatItem ?: false)
    && (!unitweaksInstalled || !tryCheckUniTweaksBoatMixinEnabled())
}
