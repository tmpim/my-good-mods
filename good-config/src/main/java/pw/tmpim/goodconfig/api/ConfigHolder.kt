package pw.tmpim.goodconfig.api

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import pw.tmpim.goodconfig.api.SyncDirection.CLIENT_TO_SERVER
import pw.tmpim.goodconfig.api.SyncDirection.SERVER_TO_CLIENT
import pw.tmpim.goodconfig.files.ConfigLoader.loadConfigFromFile
import pw.tmpim.goodutils.misc.isClient
import pw.tmpim.goodutils.misc.isServer
import java.nio.file.Path
import java.util.*

@Suppress("UNCHECKED_CAST")
class ConfigHolder<S : ConfigSpec>(
  /// client/server's local config on disk. always present. may be mutated. should be a Class
  val local: S,
  private val factory: () -> S
) {
  /*
   * client-only. configuration received from the server
   * TODO: cache server configs on disk?
   */
  var remote: S? = null
    private set

  /// server-only. per-player received configs from clients
  private val playerConfigs = WeakHashMap<String, S>()

  private val preSaveListeners = mutableListOf<(ConfigEvent<S>) -> Unit>()
  private val postLoadListeners = mutableListOf<(ConfigEvent<S>) -> Unit>()

  fun forPlayer(player: PlayerEntity?): S =
    if (!isServer || player == null) {
      local
    } else {
      playerConfigs[player.name] ?: local
    }

  // TODO: are these actually needed now that the logic lives in SchemaDelegate.getValue? is it a useful API to expose?
  @Environment(EnvType.CLIENT)
  fun <T : Any> getActiveValue(localDelegate: SchemaDelegate<T>): T {
    if (!isClient || localDelegate.syncDirection != SERVER_TO_CLIENT) {
      return localDelegate.value
    }

    val remoteSpec = remote ?: return localDelegate.value // no remote, short-circuit to local

    // find the matching delegate in the remote spec
    return (remoteSpec.findDelegateByPath(localDelegate.fullPath)?.value as? T)
      ?: localDelegate.value
  }

  @Environment(EnvType.SERVER)
  fun <T : Any> getActiveValue(localDelegate: SchemaDelegate<T>, player: PlayerEntity?): T {
    if (!isServer || localDelegate.syncDirection != CLIENT_TO_SERVER || player == null) {
      return localDelegate.value
    }

    val playerSpec = playerConfigs[player.name] ?: return localDelegate.value

    // find the matching delegate in the player spec
    return (playerSpec.findDelegateByPath(localDelegate.fullPath)?.value as? T)
      ?: localDelegate.value
  }

  fun loadFromFile(file: Path) {
    loadConfigFromFile(local, file)
    firePostLoad(local)
  }

  @Environment(EnvType.CLIENT)
  fun loadFromServer(payload: NbtCompound) {
    val spec = factory()
    spec.holder = this
    spec.role = SpecRole.REMOTE
    spec.decodeFromNbt(payload)
    remote = spec
    firePostLoad(spec)
  }

  @Environment(EnvType.CLIENT)
  fun clearFromServer() {
    remote = null
  }

  @Environment(EnvType.SERVER)
  fun loadFromPlayer(player: PlayerEntity, payload: NbtCompound) {
    val spec = factory()
    spec.holder = this
    spec.role = SpecRole.PLAYER
    spec.decodeFromNbt(payload)
    playerConfigs[player.name] = spec
    firePostLoad(spec)
  }

  @Environment(EnvType.SERVER)
  fun clearFromPlayer(player: PlayerEntity) {
    playerConfigs.remove(player.name)
  }

  fun onPreSave(listener: (event: ConfigEvent<S>) -> Unit) {
    preSaveListeners.add(listener)
  }

  fun onPostLoad(listener: (event: ConfigEvent<S>) -> Unit) {
    postLoadListeners.add(listener)
  }

  fun firePreSave(spec: S) {
    val event = ConfigEvent(spec, spec.role)
    preSaveListeners.forEach { it(event) }
  }

  fun firePostLoad(spec: S, player: PlayerEntity? = null) {
    val event = ConfigEvent(spec, spec.role, player)
    postLoadListeners.forEach { it(event) }
  }
}
