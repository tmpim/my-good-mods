package pw.tmpim.goodflags.net

import net.glasslauncher.mods.networking.GlassPacketListener
import pw.tmpim.goodutils.net.registerC2S
import pw.tmpim.goodutils.net.registerS2C

object GoodFlagsNetworking : GlassPacketListener {
  override fun registerGlassPackets() {
    with(FlagNetworkingC2S) {
      registerC2S(FLAG_UPDATE_ID, ::onFlagUpdate)
    }

    with(FlagNetworkingS2C) {
      registerS2C(FLAG_SCREEN_OPEN_ID, ::onFlagScreenOpen)
      registerS2C(FLAG_SYNC_ID, ::onFlagSync)
    }
  }
}
