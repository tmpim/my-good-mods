package pw.tmpim.goodconfig.api.delegates

import com.mojang.serialization.Codec
import pw.tmpim.goodconfig.api.SchemaDelegate
import pw.tmpim.goodconfig.api.SyncDirection
import pw.tmpim.goodconfig.codec.byteRange

internal const val DEFAULT_BYTE_VALUE = 0.toByte()
internal const val DEFAULT_BYTE_MIN = Byte.MIN_VALUE
internal const val DEFAULT_BYTE_MAX = Byte.MAX_VALUE

class ByteDelegate(
  name: String,
  description: String? = null,
  default: Byte = DEFAULT_BYTE_VALUE,
  syncDirection: SyncDirection = SyncDirection.NONE,
  requiresRestart: Boolean = false,
  val min: Byte = DEFAULT_BYTE_MIN,
  val max: Byte = DEFAULT_BYTE_MAX,
  key: String? = null,
) : SchemaDelegate<Byte>(name, description, default, syncDirection, requiresRestart, key) {
  override val codec: Codec<Byte> = byteRange(min, max)
}
