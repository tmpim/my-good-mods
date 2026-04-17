package pw.tmpim.goodconfig.api.delegates

import com.mojang.serialization.Codec
import pw.tmpim.goodconfig.api.SchemaDelegate
import pw.tmpim.goodconfig.api.SyncDirection

internal const val DEFAULT_INT_VALUE = 0
internal const val DEFAULT_INT_MIN = Int.MIN_VALUE
internal const val DEFAULT_INT_MAX = Int.MAX_VALUE

class IntDelegate(
  name: String,
  description: String? = null,
  default: Int = DEFAULT_INT_VALUE,
  syncDirection: SyncDirection = SyncDirection.NONE,
  requiresRestart: Boolean = false,
  val min: Int = DEFAULT_INT_MIN,
  val max: Int = DEFAULT_INT_MAX,
  key: String? = null,
) : SchemaDelegate<Int>(name, description, default, syncDirection, requiresRestart, key) {
  override val codec: Codec<Int> = Codec.intRange(min, max)
}
