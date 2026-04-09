package pw.tmpim.goodutils.net

import net.glasslauncher.mods.networking.GlassPacketListener

object GoodUtilsNetworking : GlassPacketListener {
  override fun registerGlassPackets() {
    with(UtilNetworkingS2C) {
      registerS2C(TRANSLATABLE_CHAT_MESSAGE_ID, ::onTranslatableChatMessage)
    }
  }
}
