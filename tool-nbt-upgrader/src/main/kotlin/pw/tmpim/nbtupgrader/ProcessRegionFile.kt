package pw.tmpim.nbtupgrader

import net.querz.mcr.MCRUtil
import net.querz.nbt.tag.CompoundTag
import net.querz.nbt.tag.ListTag
import java.io.File
import kotlin.system.measureNanoTime

fun processRegionFile(file: File): Long = measureNanoTime {
  val mcrFile = MCRUtil.read(file)
  mcrFile.forEachIndexed { index, chunk ->
    if (chunk == null) {
      // println("missing chunk $index in $file")
      return@forEachIndexed
    }

    try {
      chunk.apply {
        if (!handle.containsKey("Level")) return@apply
        val level = handle.getCompoundTag("Level")

        // block palette
        if (level.containsKey("stationapi:sections")) {
          val stationSections = level.getListTag("stationapi:sections") as ListTag<CompoundTag>
          level.put("stationapi:sections", checkCompoundList(stationSections, "stationapi:sections"))
        }

        // entities and tile entities
        entities?.let { entities = checkCompoundList(it, "Entities") }
        tileEntities?.let { tileEntities = checkCompoundList(it, "TileEntities") }
      }
    } catch (e: Exception) {
      println("chunk $index in $file: $e")
    }
  }

  MCRUtil.write(mcrFile, file)
}
