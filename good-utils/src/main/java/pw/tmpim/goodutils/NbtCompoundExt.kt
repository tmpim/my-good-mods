package pw.tmpim.goodutils

import net.minecraft.nbt.NbtCompound
import net.modificationstation.stationapi.api.util.Identifier

@Suppress("FunctionName")
interface NbtCompoundExt {
  fun `goodutils$removeTag`(name: String)
}

fun NbtCompound.removeTag(name: String) = `goodutils$removeTag`(name)

fun NbtCompound.getIdentifier(name: String) =
  Identifier.tryParse(getString(name))
fun NbtCompound.putIdentifier(name: String, identifier: Identifier) =
  putString(name, identifier.toString())
