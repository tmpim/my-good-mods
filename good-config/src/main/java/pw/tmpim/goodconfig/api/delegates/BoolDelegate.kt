package pw.tmpim.goodconfig.api.delegates

import com.mojang.serialization.Codec
import pw.tmpim.goodconfig.api.SchemaDelegate
import pw.tmpim.goodconfig.api.SyncDirection

internal const val DEFAULT_BOOLEAN_VALUE = false

class BoolDelegate(
  name: String,
  description: String? = null,
  default: Boolean = DEFAULT_BOOLEAN_VALUE,
  syncDirection: SyncDirection = SyncDirection.NONE,
  requiresRestart: Boolean = false,
  key: String? = null,
) : SchemaDelegate<Boolean>(name, description, default, syncDirection, requiresRestart, key) {
  override val codec: Codec<Boolean> = Codec.BOOL
}
