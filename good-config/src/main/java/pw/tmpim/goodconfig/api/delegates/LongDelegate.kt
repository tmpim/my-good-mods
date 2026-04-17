package pw.tmpim.goodconfig.api.delegates

import com.mojang.serialization.Codec
import pw.tmpim.goodconfig.api.SchemaDelegate
import pw.tmpim.goodconfig.api.SyncDirection
import pw.tmpim.goodconfig.codec.longRange

internal const val DEFAULT_LONG_VALUE = 0L
internal const val DEFAULT_LONG_MIN = Long.MIN_VALUE
internal const val DEFAULT_LONG_MAX = Long.MAX_VALUE

class LongDelegate(
  name: String,
  description: String? = null,
  default: Long = DEFAULT_LONG_VALUE,
  syncDirection: SyncDirection = SyncDirection.NONE,
  requiresRestart: Boolean = false,
  val min: Long = DEFAULT_LONG_MIN,
  val max: Long = DEFAULT_LONG_MAX,
  key: String? = null,
) : SchemaDelegate<Long>(name, description, default, syncDirection, requiresRestart, key) {
  override val codec: Codec<Long> = longRange(min, max)
}
