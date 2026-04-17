package pw.tmpim.goodconfig.api.delegates

import com.mojang.serialization.Codec
import pw.tmpim.goodconfig.api.SchemaDelegate
import pw.tmpim.goodconfig.api.SyncDirection

internal const val DEFAULT_FLOAT_VALUE = 0.0f
internal const val DEFAULT_FLOAT_MIN = Float.MIN_VALUE
internal const val DEFAULT_FLOAT_MAX = Float.MAX_VALUE

class FloatDelegate(
  name: String,
  description: String? = null,
  default: Float = DEFAULT_FLOAT_VALUE,
  syncDirection: SyncDirection = SyncDirection.NONE,
  requiresRestart: Boolean = false,
  val min: Float = DEFAULT_FLOAT_MIN,
  val max: Float = DEFAULT_FLOAT_MAX,
  key: String? = null,
) : SchemaDelegate<Float>(name, description, default, syncDirection, requiresRestart, key) {
  override val codec: Codec<Float> = Codec.floatRange(min, max)
}
