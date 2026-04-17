package pw.tmpim.goodconfig.api

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import net.modificationstation.stationapi.api.nbt.NbtOps
import pw.tmpim.goodconfig.GoodConfig.log
import pw.tmpim.goodconfig.api.SyncDirection.CLIENT_TO_SERVER
import pw.tmpim.goodconfig.api.SyncDirection.SERVER_TO_CLIENT
import pw.tmpim.goodconfig.codec.TomlOps
import pw.tmpim.goodutils.misc.isClient
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

abstract class SchemaDelegate<T : Any>(
  val name: String,
  val description: String? = null,

  val default: T,

  val syncDirection: SyncDirection = SyncDirection.NONE,
  val requiresRestart: Boolean = false,

  /** override the serialised key for this property */
  private val key: String? = null,
) : ReadWriteProperty<Any?, T>, HierarchyNode {
  override var parent: BaseContainerSpec? = null

  var serialisedKey: String = key ?: name // 'name' used temporarily until provideDelegate called
    private set

  override val fullPath: String
    get() {
      val p = parent?.fullPath
      return if (p.isNullOrEmpty()) serialisedKey else "$p.$serialisedKey"
    }

  var value: T = default
    set(v) {
      /*
        TODO: DFU v9

        codec.encodeStart(NbtOps.INSTANCE, v).getOrThrow {
          IllegalArgumentException("failed to validate `$fullPath` with NbtOps: $it")
        }

        codec.encodeStart(TomlOps, v).getOrThrow {
          IllegalArgumentException("failed to validate `$fullPath` with TomlOps: $it")
        }
       */

      // attempt to serialise it to perform validation
      codec.encodeStart(NbtOps.INSTANCE, v).getOrThrow(true) {
        log.error("failed to validate `$fullPath` with NbtOps: $it")
      }
      codec.encodeStart(TomlOps, v).getOrThrow(true) {
        log.error("failed to validate `$fullPath` with TomlOps: $it")
      }

      field = v
    }

  fun findRootSpec(): ConfigSpec? {
    var current: BaseContainerSpec? = parent

    while (current != null) {
      if (current is ConfigSpec) return current
      current = current.parent // 登れ　進め　高い塔へ
    }

    return null
  }

  /** delegates contextually based on the spec */
  @Suppress("UNCHECKED_CAST")
  override fun getValue(thisRef: Any?, property: KProperty<*>): T {
    val root = findRootSpec() ?: return value
    val holder = root.holder ?: return value

    return when (root.role) {
      SpecRole.LOCAL -> {
        // client: read the local disk config unless the server has an override
        if (isClient && syncDirection == SERVER_TO_CLIENT) {
          // find the matching delegate in the remote spec
          holder.remote?.findDelegateByPath(fullPath)?.value as? T ?: value
        } else {
          value // local config value
        }
      }

      SpecRole.PLAYER -> {
        // server: we're reading a specific player's config. return the value directly if it's synced, otherwise
        //         delegate to the server's local config
        if (syncDirection == CLIENT_TO_SERVER) {
          value
        } else {
          // find the matching delegate in the local spec
          holder.local.findDelegateByPath(fullPath)?.value as? T ?: value
        }
      }

      SpecRole.REMOTE -> {
        // direct read of a remote spec, probably unneeded?
        value
      }
    }
  }

  override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
    this.value = value
  }

  open fun onKeyRenamed(oldKey: String, newKey: String) {}

  operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): SchemaDelegate<T> {
    // if the user didn't specify a serialisation key, we need to set the new one based on the property's field name,
    // and properly update it in the parent's entry map, as well as the spec of the container if we are one
    if (key == null && serialisedKey != property.name) {
      val oldKey = serialisedKey
      serialisedKey = property.name

      // update the parent's entry map
      parent?.renameDelegateKey(oldKey, serialisedKey)

      // rename the container
      onKeyRenamed(oldKey, serialisedKey)
    }

    return this
  }

  protected abstract val codec: Codec<T>

  fun <O> encodeValue(ops: DynamicOps<O>): DataResult<O> =
    codec.encodeStart(ops, value /* always use the backing-value, not the holder-aware value */)

  fun <O> decodeAndStore(ops: DynamicOps<O>, input: O) {
    val result = codec.parse(ops, input)

    result.error().ifPresent { err ->
      log.error("failed to parse $fullPath in ${findRootSpec()?.file ?: this::class.java.simpleName}", err)
    }

    // allow partial parsing
    result.result().ifPresent { value = it }
  }
}
