package pw.tmpim.goodfarming.net

import net.glasslauncher.mods.networking.GlassPacketListener
import pw.tmpim.goodutils.net.registerC2S

object GoodFarmingNetworking : GlassPacketListener {
  override fun registerGlassPackets() {
    with(GoodFarmingNetworkingC2S) {
      registerC2S(PLAYER_CONFIGURATION, ::onPlayerConfiguration)
    }
  }
}
