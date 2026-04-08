package pw.tmpim.goodutils.nbt

import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.modificationstation.stationapi.api.nbt.NbtIntArray
import net.modificationstation.stationapi.api.util.Identifier
import pw.tmpim.goodutils.misc.toIntArray
import pw.tmpim.goodutils.misc.toUuid
import pw.tmpim.goodutils.misc.tryParseUuid
import java.util.*

@Suppress("FunctionName")
interface NbtCompoundExtensions {
  fun `goodutils$removeTag`(key: String)
}

object NbtType {
  const val END        = 0
  const val BYTE       = 1
  const val SHORT      = 2
  const val INT        = 3
  const val LONG       = 4
  const val FLOAT      = 5
  const val DOUBLE     = 6
  const val BYTE_ARRAY = 7
  const val STRING     = 8
  const val LIST       = 9
  const val COMPOUND   = 10

  // post b1.7.3, backported by stapi:
  const val INT_ARRAY  = 11
  const val LONG_ARRAY = 12

  /** Any numeric value: byte, short, int, long, float, double. */
  const val NUMBER = 99
}

// missing functionality
fun NbtCompound.remove(key: String) = `goodutils$removeTag`(key)

fun NbtCompound.getType(key: String) =
  (entries[key] as? NbtElement)?.type ?: NbtType.END

fun NbtCompound.contains(key: String, type: Int) =
  when (val i = getType(key)) {
    type -> true
    99   -> with (NbtType) { i == BYTE || i == SHORT || i == INT || i == LONG || i == FLOAT || i == DOUBLE }
    else -> false
  }

// ┌──────────────────────────────────────────────────────────┐
// │                  additional primitives                   │
// └──────────────────────────────────────────────────────────┘

// Identifier
fun NbtCompound.getId(key: String) =
  Identifier.tryParse(getString(key))
fun NbtCompound.putId(key: String, id: Identifier) =
  putString(key, id.toString())

// UUID as string (more debug-friendly)
fun NbtCompound.getUuidAsString(key: String): UUID? =
  tryParseUuid(getString(key))
fun NbtCompound.putUuidAsString(key: String, id: UUID) =
  putString(key, id.toString())

// UUID as int array (more space-efficient)
fun NbtCompound.containsUuidAsArray(key: String): Boolean =
  (entries[key] as? NbtIntArray)?.data?.size == 4
fun NbtCompound.getUuidAsArray(key: String): UUID? =
  if (containsUuidAsArray(key)) getIntArray(key).toUuid() else null
fun NbtCompound.putUuidAsArray(key: String, uuid: UUID) =
  put(key, uuid.toIntArray())

// ┌──────────────────────────────────────────────────────────┐
// │                    useful conversions                    │
// └──────────────────────────────────────────────────────────┘

fun NbtCompound.byteToDouble(key: String) =
  getByte(key).toDouble()

// ┌──────────────────────────────────────────────────────────┐
// │         optional (nullable) getters and setters          │
// └──────────────────────────────────────────────────────────┘

fun NbtCompound.optBoolean(key: String): Boolean? =
  if (contains(key, NbtType.BYTE)) getByte(key) != 0.toByte() else null
fun NbtCompound.optInt(key: String): Int? =
  if (contains(key, NbtType.INT)) getInt(key) else null
fun NbtCompound.optString(key: String): String? =
  if (contains(key, NbtType.STRING)) getString(key) else null
fun NbtCompound.optCompound(key: String): NbtCompound? =
  if (contains(key, NbtType.COMPOUND)) getCompound(key) else null

fun NbtCompound.putOptInt(key: String, value: Int?) { value?.let { putInt(key, it) } }
fun NbtCompound.putOptString(key: String, value: String?) { value?.let { putString(key, it) } }
fun NbtCompound.putOptUuidAsString(key: String, value: UUID?) { value?.let { putUuidAsString(key, it) } }
fun NbtCompound.putOptUuidAsArray(key: String, value: UUID?) { value?.let { putUuidAsArray(key, it) } }

/** if the value is null, the tag will be cleared from the existing compound */
fun NbtCompound.putNullableCompound(key: String, value: NbtCompound?) {
  if (value != null) put(key, value) else remove(key)
}

/** if the value is null, the tag will be cleared from the existing compound */
fun NbtCompound.putNullableUuidAsString(key: String, value: UUID?) {
  if (value != null) putUuidAsString(key, value) else remove(key)
}

/** if the value is null, the tag will be cleared from the existing compound */
fun NbtCompound.putNullableUuidAsArray(key: String, value: UUID?) {
  if (value != null) putUuidAsArray(key, value) else remove(key)
}
