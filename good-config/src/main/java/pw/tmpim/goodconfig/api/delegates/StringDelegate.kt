package pw.tmpim.goodconfig.api.delegates

import com.mojang.serialization.Codec
import pw.tmpim.goodconfig.api.SchemaDelegate
import pw.tmpim.goodconfig.api.SyncDirection
import pw.tmpim.goodconfig.codec.string

internal const val DEFAULT_STRING_VALUE = ""
internal const val DEFAULT_STRING_MIN_LENGTH = 0
internal const val DEFAULT_STRING_MAX_LENGTH = 32767

class StringDelegate(
  name: String,
  description: String? = null,
  default: String = DEFAULT_STRING_VALUE,
  syncDirection: SyncDirection = SyncDirection.NONE,
  requiresRestart: Boolean = false,
  val minLength: Int = DEFAULT_STRING_MIN_LENGTH,
  val maxLength: Int = DEFAULT_STRING_MAX_LENGTH,
  key: String? = null,
) : SchemaDelegate<String>(name, description, default, syncDirection, requiresRestart, key) {
  // TODO: DFU v9:
  // override val codec: Codec<String> = Codec.string(minLength, maxLength)
  override val codec: Codec<String> = string(minLength, maxLength)
}
