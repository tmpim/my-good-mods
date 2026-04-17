package pw.tmpim.goodconfig.api.delegates

import com.mojang.serialization.Codec
import pw.tmpim.goodconfig.api.SchemaDelegate
import pw.tmpim.goodconfig.api.SyncDirection
import pw.tmpim.goodconfig.codec.shortRange

internal const val DEFAULT_SHORT_VALUE = 0.toShort()
internal const val DEFAULT_SHORT_MIN = Short.MIN_VALUE
internal const val DEFAULT_SHORT_MAX = Short.MAX_VALUE

class ShortDelegate(
  name: String,
  description: String? = null,
  default: Short = DEFAULT_SHORT_VALUE,
  syncDirection: SyncDirection = SyncDirection.NONE,
  requiresRestart: Boolean = false,
  val min: Short = DEFAULT_SHORT_MIN,
  val max: Short = DEFAULT_SHORT_MAX,
  key: String? = null,
) : SchemaDelegate<Short>(name, description, default, syncDirection, requiresRestart, key) {
  override val codec: Codec<Short> = shortRange(min, max)
}
