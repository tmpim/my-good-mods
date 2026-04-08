package pw.tmpim.goodutils.net

import net.glasslauncher.mods.networking.GlassNetworking
import net.glasslauncher.mods.networking.GlassPacket
import net.glasslauncher.mods.networking.GlassPacketListener
import net.minecraft.client.network.ClientNetworkHandler
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.NetworkHandler
import net.minecraft.server.network.ServerPlayNetworkHandler
import net.modificationstation.stationapi.api.util.Identifier

/** client --> server (server bound) */
inline fun GlassPacketListener.registerC2S(
  id: Identifier,
  crossinline handler: (GlassPacket, ServerPlayNetworkHandler) -> Unit
) = registerGlassPacket(
  id.toString(),
  { id, unknownHandler ->
    handler(id, checkNotNull(unknownHandler as? ServerPlayNetworkHandler) {
      "handler is not a ServerPlayNetworkHandler" // TODO: what about login?
    })
  },
  false,
  true // server bound
)

/** server --> client (client bound) */
inline fun GlassPacketListener.registerS2C(
  id: Identifier,
  crossinline handler: (GlassPacket, ClientNetworkHandler) -> Unit
) = registerGlassPacket(
  id.toString(),
  { id, unknownHandler ->
    handler(id, checkNotNull(unknownHandler as? ClientNetworkHandler) {
      "handler is not a ClientNetworkHandler"
    })
  },
  true, // client bound
  false
)

/** bidirectional (client and server bound) */
fun GlassPacketListener.register(
  id: Identifier,
  handler: (GlassPacket, NetworkHandler) -> Unit
) = registerGlassPacket(id.toString(), handler, true, true)

/**
 * convenience constructor for GlassPacket with an identifier. yes we split it just to join it just to split it just to
 * join it again.
 */
fun GlassPacket(id: Identifier, nbt: NbtCompound = NbtCompound()): GlassPacket =
  GlassPacket<GlassPacket /* idk lol */>(id.namespace.toString(), id.path, nbt)

fun GlassPacket(id: Identifier, nbtApplier: NbtCompound.(nbt: NbtCompound) -> Any): GlassPacket =
  GlassPacket<GlassPacket>(id.namespace.toString(), id.path, NbtCompound().also { it.nbtApplier(it) })

/** convenience extensions for sending */
fun GlassPacket.sendToPlayer(player: PlayerEntity) = GlassNetworking.sendToPlayer(player, this)
fun GlassPacket.sendToServer() = GlassNetworking.sendToServer(this)
