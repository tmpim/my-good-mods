package pw.tmpim.goodutils

import net.minecraft.nbt.NbtCompound

@Suppress("FunctionName")
interface NbtCompoundExt {
  fun `goodutils$removeTag`(name: String)
}

fun NbtCompound.removeTag(name: String) = `goodutils$removeTag`(name)
