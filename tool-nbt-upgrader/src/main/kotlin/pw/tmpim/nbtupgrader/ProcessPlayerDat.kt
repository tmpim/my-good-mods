package pw.tmpim.nbtupgrader

import net.querz.nbt.io.NBTUtil
import net.querz.nbt.tag.CompoundTag
import net.querz.nbt.tag.ListTag
import java.io.File
import kotlin.system.measureNanoTime

fun processPlayerDat(file: File): Long = measureNanoTime {
  val nbtFile = NBTUtil.read(file)
  val root = requireNotNull(nbtFile.tag as? CompoundTag) { "invalid player.dat $file" }

  if (root.containsKey("Inventory")) {
    val inventory = (root.getListTag("Inventory") as? ListTag<CompoundTag>)
      ?.takeIf { it.typeClass == CompoundTag::class.java }

    if (inventory != null) {
      root.put("Inventory", checkCompoundList(inventory, "Inventory"))
    }
  } else {
    println("no inventory in $file")
  }

  NBTUtil.write(nbtFile, file)
}
