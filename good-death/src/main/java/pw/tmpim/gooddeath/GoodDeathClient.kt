package pw.tmpim.gooddeath

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.api.ModInitializer
import net.mine_diver.unsafeevents.listener.EventListener
import net.minecraft.client.network.MultiplayerClientPlayerEntity
import net.minecraft.client.option.KeyBinding
import net.modificationstation.stationapi.api.client.event.keyboard.KeyStateChangedEvent
import net.modificationstation.stationapi.api.client.event.option.KeyBindingRegisterEvent
import net.modificationstation.stationapi.api.event.mod.InitEvent
import org.lwjgl.input.Keyboard
import pw.tmpim.gooddeath.GoodDeath.MOD_ID
import pw.tmpim.gooddeath.GoodDeath.MOD_NAME
import pw.tmpim.gooddeath.GoodDeath.log
import pw.tmpim.goodutils.misc.loader
import pw.tmpim.goodutils.misc.mcClient

object GoodDeathClient : ModInitializer {
  // quick debug hotkey to kill the player in dev (singleplayer only)
  val keyDebugSuicide = KeyBinding("key.$MOD_ID.suicide", Keyboard.KEY_B) // b for bucket

  override fun onInitialize() {}

  @EventListener
  fun onInit(event: InitEvent) {
    log.info("$MOD_NAME initializing client")
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
