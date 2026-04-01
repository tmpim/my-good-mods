package pw.tmpim.nbtupgrader

import java.io.File

fun findRegionFiles(regionDir: File) = regionDir.listFiles()
  ?.filter { it.name.endsWith(".mcr") }
  ?.takeIf { it.isNotEmpty() }

fun findNbtFiles(dir: File) = dir.listFiles()
  ?.filter { it.name.endsWith(".dat") }
  .orEmpty()
