package pw.tmpim.nbtupgrader

import net.querz.nbt.tag.CompoundTag
import net.querz.nbt.tag.ListTag
import net.querz.nbt.tag.StringTag
import net.querz.nbt.tag.Tag

fun checkCompound(data: CompoundTag, path: String): CompoundTag {
  val newData = CompoundTag()

  // Continue recursively checking all the data
  data.forEach { (key, value) ->
    val newKey = stringReplacements[key]
      ?.also { println("$path.$key -> $path.$it") }
      ?: key

    val newValue: Tag<*> = when (value) {
      is StringTag ->
        StringTag(stringReplacements[value.value]
          ?.also { println("$path.$key: \"${value.value}\" -> \"$it\"") }
          ?: value.value)

      is CompoundTag ->
        checkCompound(value, "$path.$newKey")

      is ListTag<*> ->
        when {
          value.typeClass == CompoundTag::class.java ->
            checkCompoundList(value as ListTag<CompoundTag>, "$path.$newKey")
          value.typeClass == StringTag::class.java ->
            checkStringList(value as ListTag<StringTag>, "$path.$newKey")
          else -> value
        }

      else -> value
    }

    newData.put(newKey, newValue)
  }

  return newData
}

fun checkCompoundList(data: ListTag<CompoundTag>, path: String): ListTag<CompoundTag> {
  val newList = ListTag(CompoundTag::class.java, data.size())

  data.forEachIndexed { index, it ->
    newList.add(checkCompound(it, "$path[$index]"))
  }

  return newList
}

fun checkStringList(data: ListTag<StringTag>, path: String): ListTag<StringTag> {
  val newList = ListTag(StringTag::class.java, data.size())

  data.forEachIndexed { index, it ->
    newList.add(StringTag(stringReplacements[it.value]
      ?.also { repl -> println("$path[$index]: \"${it.value}\" \"$repl\"") }
      ?: it.value))
  }

  return newList
}
