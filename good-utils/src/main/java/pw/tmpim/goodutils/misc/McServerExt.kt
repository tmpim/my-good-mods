package pw.tmpim.goodutils.misc

import net.minecraft.server.MinecraftServer

// accessor for the game. if you have a nearby instance of this, its better to use that instead of this
val mcServer by lazy {
  check(isServer) { "do not access mcServer on a client" }
  val game = loader.gameInstance
  checkNotNull(game as? MinecraftServer) { "unexpected game class: ${game::class.java.name}" }
}
