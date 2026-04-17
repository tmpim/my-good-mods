package pw.tmpim.goodconfig.api.delegates

import com.mojang.serialization.Codec
import pw.tmpim.goodconfig.api.SchemaDelegate
import pw.tmpim.goodconfig.api.SyncDirection

internal const val DEFAULT_DOUBLE_VALUE = 0.0
internal const val DEFAULT_DOUBLE_MIN = Double.MIN_VALUE
internal const val DEFAULT_DOUBLE_MAX = Double.MAX_VALUE

class DoubleDelegate(
  name: String,
  description: String? = null,
  default: Double = DEFAULT_DOUBLE_VALUE,
  syncDirection: SyncDirection = SyncDirection.NONE,
  requiresRestart: Boolean = false,
  val min: Double = DEFAULT_DOUBLE_MIN,
  val max: Double = DEFAULT_DOUBLE_MAX,
  key: String? = null,
) : SchemaDelegate<Double>(name, description, default, syncDirection, requiresRestart, key) {
  override val codec: Codec<Double> = Codec.doubleRange(min, max)
}
