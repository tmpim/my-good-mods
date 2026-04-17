package pw.tmpim.goodconfig.api

import pw.tmpim.goodconfig.api.delegates.*
import pw.tmpim.goodconfig.codec.buildConfigCodec

abstract class BaseContainerSpec : HierarchyNode {
  override var parent: BaseContainerSpec? = null

  internal var containerName: String? = null

  override val fullPath: String by lazy {
    val p = parent?.fullPath
    when {
      p.isNullOrEmpty()     -> containerName ?: "" // root spec or top-level container
      containerName != null -> "$p.$containerName" // nested container path
      else -> p                                    // fallback (shouldn't happen)
    }
  }

  @Suppress("PropertyName")
  @PublishedApi
  internal var _entries = LinkedHashMap<String, SchemaDelegate<*>>()

  val entries: Map<String, SchemaDelegate<*>>
    get() = _entries

  val codec by lazy { buildConfigCodec { _entries } }

  /** add a delegate and set its parent */
  fun <T : SchemaDelegate<*>> registerDelegate(delegate: T): T {
    delegate.parent = this
    _entries[delegate.serialisedKey] = delegate
    return delegate
  }

  fun findDelegateByPath(path: String): SchemaDelegate<*>? {
    val parts = path.split(".")
    var currentSpec: BaseContainerSpec = this

    for (i in parts.indices) {
      val part = parts[i]
      val delegate = currentSpec._entries[part] ?: return null

      // if this is the final piece of the path, we've found our delegate
      if (i == parts.lastIndex) {
        return delegate
      }

      // otherwise, we need to dig deeper. check if the delegate contains a nested spec
      currentSpec = when (delegate) {
        is ContainerDelegate<*> -> delegate.spec
        else                    -> return null // path tried to go "inside" a primitive
      }
    }

    return null
  }

  internal fun renameDelegateKey(oldKey: String, newKey: String) {
    if (!_entries.containsKey(oldKey)) return

    // rebuild the map to preserve the original TOML insertion order
    // TODO: is this going to be expensive? should only happen once on load with relatively small maps. we can maintain
    //       two lists to cheaply rename keys instead of maintaining a LinkedHashMap?
    val newEntries = LinkedHashMap<String, SchemaDelegate<*>>()
    _entries.forEach { (k, v) ->
      if (k == oldKey) {
        newEntries[newKey] = v
      } else {
        newEntries[k] = v
      }
    }

    _entries = newEntries // swap in the new map
  }

  // ┌──────────────────────────────────────────────────────────┐
  // │                        primitives                        │
  // └──────────────────────────────────────────────────────────┘
  fun bool(
    name: String,
    description: String? = null,
    default: Boolean = DEFAULT_BOOLEAN_VALUE,
    syncDirection: SyncDirection = SyncDirection.NONE,
    requiresRestart: Boolean = false,
    key: String? = null,
  ) = registerDelegate(
    BoolDelegate(
      name = name,
      description = description,
      default = default,
      syncDirection = syncDirection,
      requiresRestart = requiresRestart,
      key = key,
    )
  )

  fun byte(
    name: String,
    description: String? = null,
    default: Byte = DEFAULT_BYTE_VALUE,
    syncDirection: SyncDirection = SyncDirection.NONE,
    requiresRestart: Boolean = false,
    min: Byte = DEFAULT_BYTE_MIN,
    max: Byte = DEFAULT_BYTE_MAX,
    key: String? = null,
  ) = registerDelegate(
    ByteDelegate(
      name = name,
      description = description,
      default = default,
      syncDirection = syncDirection,
      requiresRestart = requiresRestart,
      min = min,
      max = max,
      key = key,
    )
  )

  fun double(
    name: String,
    description: String? = null,
    default: Double = DEFAULT_DOUBLE_VALUE,
    syncDirection: SyncDirection = SyncDirection.NONE,
    requiresRestart: Boolean = false,
    min: Double = DEFAULT_DOUBLE_MIN,
    max: Double = DEFAULT_DOUBLE_MAX,
    key: String? = null,
  ) = registerDelegate(
    DoubleDelegate(
      name = name,
      description = description,
      default = default,
      syncDirection = syncDirection,
      requiresRestart = requiresRestart,
      min = min,
      max = max,
      key = key,
    )
  )

  fun float(
    name: String,
    description: String? = null,
    default: Float = DEFAULT_FLOAT_VALUE,
    syncDirection: SyncDirection = SyncDirection.NONE,
    requiresRestart: Boolean = false,
    min: Float = DEFAULT_FLOAT_MIN,
    max: Float = DEFAULT_FLOAT_MAX,
    key: String? = null,
  ) = registerDelegate(
    FloatDelegate(
      name = name,
      description = description,
      default = default,
      syncDirection = syncDirection,
      requiresRestart = requiresRestart,
      min = min,
      max = max,
      key = key,
    )
  )

  fun int(
    name: String,
    description: String? = null,
    default: Int = DEFAULT_INT_VALUE,
    syncDirection: SyncDirection = SyncDirection.NONE,
    requiresRestart: Boolean = false,
    min: Int = DEFAULT_INT_MIN,
    max: Int = DEFAULT_INT_MAX,
    key: String? = null,
  ) = registerDelegate(
    IntDelegate(
      name = name,
      description = description,
      default = default,
      syncDirection = syncDirection,
      requiresRestart = requiresRestart,
      min = min,
      max = max,
      key = key,
    )
  )

  fun long(
    name: String,
    description: String? = null,
    default: Long = DEFAULT_LONG_VALUE,
    syncDirection: SyncDirection = SyncDirection.NONE,
    requiresRestart: Boolean = false,
    min: Long = DEFAULT_LONG_MIN,
    max: Long = DEFAULT_LONG_MAX,
    key: String? = null,
  ) = registerDelegate(
    LongDelegate(
      name = name,
      description = description,
      default = default,
      syncDirection = syncDirection,
      requiresRestart = requiresRestart,
      min = min,
      max = max,
      key = key,
    )
  )

  fun short(
    name: String,
    description: String? = null,
    default: Short = DEFAULT_SHORT_VALUE,
    syncDirection: SyncDirection = SyncDirection.NONE,
    requiresRestart: Boolean = false,
    min: Short = DEFAULT_SHORT_MIN,
    max: Short = DEFAULT_SHORT_MAX,
    key: String? = null,
  ) = registerDelegate(
    ShortDelegate(
      name = name,
      description = description,
      default = default,
      syncDirection = syncDirection,
      requiresRestart = requiresRestart,
      min = min,
      max = max,
      key = key,
    )
  )

  fun string(
    name: String,
    description: String? = null,
    default: String = DEFAULT_STRING_VALUE,
    syncDirection: SyncDirection = SyncDirection.NONE,
    requiresRestart: Boolean = false,
    minLength: Int = DEFAULT_STRING_MIN_LENGTH,
    maxLength: Int = DEFAULT_STRING_MAX_LENGTH,
    key: String? = null,
  ) = registerDelegate(
    StringDelegate(
      name = name,
      description = description,
      default = default,
      syncDirection = syncDirection,
      requiresRestart = requiresRestart,
      minLength = minLength,
      maxLength = maxLength,
      key = key,
    )
  )

  // ┌──────────────────────────────────────────────────────────┐
  // │                          other                           │
  // └──────────────────────────────────────────────────────────┘
  inline fun <reified E : Enum<E>> enum(
    name: String,
    description: String? = null,
    default: E = enumValues<E>().first(), // throws if empty
    syncDirection: SyncDirection = SyncDirection.NONE,
    requiresRestart: Boolean = false,
    enumClass: Class<E>,
    key: String? = null,
  ) = registerDelegate(
    EnumDelegate(
      name = name,
      description = description,
      default = default,
      syncDirection = syncDirection,
      requiresRestart = requiresRestart,
      enumClass = enumClass,
      key = key,
    )
  )

  // ┌──────────────────────────────────────────────────────────┐
  // │                           rows                           │
  // └──────────────────────────────────────────────────────────┘
  fun <R : RowSpec> row(
    name: String,
    description: String = "",
    key: String? = null,
    specFactory: () -> R,
    block: R.() -> Unit,
  ): RowDelegate<R> {
    val spec = specFactory().apply(block)
    spec.parent = this
    spec.containerName = key ?: name

    return registerDelegate(
      RowDelegate(
        name = name,
        description = description,
        key = key
      ) { spec }
    )
  }

  fun row(
    name: String,
    description: String = "",
    key: String? = null,
    block: RowSpec.() -> Unit,
  ): RowDelegate<RowSpec> =
    row(
      name = name,
      description = description,
      key = key,
      specFactory = ::RowSpec,
      block = block
    )

  // ┌──────────────────────────────────────────────────────────┐
  // │                        categories                        │
  // └──────────────────────────────────────────────────────────┘
  fun <C : ConfigSpec> category(
    name: String,
    spec: C,
    key: String? = null,
  ): CategoryDelegate<C> {
    spec.parent = this
    spec.containerName = key ?: name

    return registerDelegate(
      CategoryDelegate(
        name = name,
        spec = spec,
        key = key
      )
    )
  }
}
