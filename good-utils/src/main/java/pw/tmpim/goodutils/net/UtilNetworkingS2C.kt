package pw.tmpim.goodutils.net

import net.glasslauncher.mods.networking.GlassPacket
import net.minecraft.client.Minecraft
import net.minecraft.client.network.ClientNetworkHandler
import net.minecraft.nbt.NbtList
import net.minecraft.nbt.NbtString
import pw.tmpim.goodutils.GoodUtils.namespace
import pw.tmpim.goodutils.i18n.i18n

object UtilNetworkingS2C {
  val TRANSLATABLE_CHAT_MESSAGE_ID = namespace.id("translatable_chat_message")

  fun createTranslatableChatMessage(key: String, args: Array<String>) = GlassPacket(TRANSLATABLE_CHAT_MESSAGE_ID) {
    putString("key", key)
    put("args", NbtList().apply {
      for (arg in args) add(NbtString(arg))
    })
  }

  fun onTranslatableChatMessage(packet: GlassPacket, handler: ClientNetworkHandler?) {
    val list = packet.nbt.getList("args")
    val args = mutableListOf<String>().apply {
      if (list.size() > 0) for (i in 0 until list.size()) {
        add(list.get(i).toString())
      }
    }
    (handler?.minecraft ?: Minecraft.INSTANCE).inGameHud
      .addChatMessage(packet.nbt.getString("key").i18n(*args.toTypedArray()))
  }
}
