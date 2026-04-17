package pw.tmpim.goodconfig.api.delegates

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import pw.tmpim.goodconfig.api.SchemaDelegate
import pw.tmpim.goodconfig.api.SyncDirection

class EnumDelegate<E : Enum<E>>(
  name: String,
  description: String? = null,
  default: E,
  syncDirection: SyncDirection = SyncDirection.NONE,
  requiresRestart: Boolean = false,
  val enumClass: Class<E>,
  key: String? = null,
) : SchemaDelegate<E>(name, description, default, syncDirection, requiresRestart, key) {
  override val codec: Codec<E> = Codec.STRING.flatXmap(
    { s ->
      try {
        DataResult.success(java.lang.Enum.valueOf(enumClass, s.uppercase()))
      } catch (_: IllegalArgumentException) {
        DataResult.error { "unknown enum constant '$s' for ${enumClass.simpleName}" }
      }
    },
    { e -> DataResult.success(e.name) }
  )
}
