package pw.tmpim.gooddeath

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.glasslauncher.mods.gcapi3.api.ConfigRoot
import net.mine_diver.unsafeevents.listener.EventListener
import net.minecraft.block.Block
import net.minecraft.client.network.MultiplayerClientPlayerEntity
import net.minecraft.client.option.KeyBinding
import net.modificationstation.stationapi.api.client.event.keyboard.KeyStateChangedEvent
import net.modificationstation.stationapi.api.client.event.option.KeyBindingRegisterEvent
import net.modificationstation.stationapi.api.event.block.entity.BlockEntityRegisterEvent
import net.modificationstation.stationapi.api.event.mod.InitEvent
import net.modificationstation.stationapi.api.event.registry.BlockRegistryEvent
import net.modificationstation.stationapi.api.util.Namespace
import org.lwjgl.input.Keyboard
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import pw.tmpim.gooddeath.block.TombstoneBlock
import pw.tmpim.gooddeath.block.TombstoneBlockEntity
import pw.tmpim.goodutils.misc.loader
import pw.tmpim.goodutils.misc.mcClient

object GoodDeath : ModInitializer {
  const val MOD_ID = "good-death"
  const val MOD_NAME = "Good Death"
  val MOD_VERSION = FabricLoader.getInstance().getModContainer(MOD_ID).get().metadata.version.toString()

  val namespace: Namespace = Namespace.resolve()

  lateinit var tombstoneBlock: Block

  @JvmField val log: Logger = LoggerFactory.getLogger(MOD_ID)

  @JvmStatic
  @ConfigRoot(value = MOD_ID, visibleName = MOD_NAME, nameKey = "$CONFIG_KEY.name")
  val config = GoodDeathConfig()

  // quick debug hotkey to kill the player in dev (singleplayer only)
  val keyDebugSuicide = KeyBinding("key.$MOD_ID.suicide", Keyboard.KEY_B) // b for bucket

  override fun onInitialize() {}

  @EventListener
  fun onInit(event: InitEvent) {
    log.info("$MOD_NAME initializing")
  }

  @EventListener
  fun onRegisterBlocks(event: BlockRegistryEvent) {
    log.info("$MOD_NAME registering blocks")

    tombstoneBlock = TombstoneBlock()
  }

  @EventListener
  fun onRegisterBlockEntities(event: BlockEntityRegisterEvent) {
    event.register(namespace.id("tombstone"), TombstoneBlockEntity::class.java)
  }

  @EventListener
  @Environment(EnvType.CLIENT)
  fun onKeyBindingRegister(event: KeyBindingRegisterEvent) {
    if (loader.isDevelopmentEnvironment) {
      event.keyBindings.add(keyDebugSuicide)
    }
  }

  @EventListener
  @Environment(EnvType.CLIENT)
  fun onKeyStateChangedEvent(event: KeyStateChangedEvent) {
    val player = mcClient.player

    if (
      loader.isDevelopmentEnvironment
      && event.environment == KeyStateChangedEvent.Environment.IN_GAME
      && player !is MultiplayerClientPlayerEntity
      && Keyboard.isKeyDown(keyDebugSuicide.code)
    ) {
      player.damage(null, 1000)
    }
  }
}
