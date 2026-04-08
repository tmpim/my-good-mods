package pw.tmpim.goodutils.misc

import net.minecraft.client.Minecraft

// accessor for the game. if you have a nearby instance of this, its better to use that instead of this
val mcClient by lazy {
  check(isClient) { "do not access mcClient on a server" }
  val game = loader.gameInstance
  checkNotNull(game as? Minecraft) { "unexpected game class: ${game::class.java.name}" }
}
