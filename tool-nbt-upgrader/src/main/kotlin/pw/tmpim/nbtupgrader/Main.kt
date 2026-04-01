package pw.tmpim.nbtupgrader

import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.system.measureNanoTime

val maxThreads = System.getenv("MAX_THREADS")?.toInt()
  ?: (Runtime.getRuntime().availableProcessors() / 2).coerceAtLeast(2)

const val manualGcThreshold = 32 // Number of region files to process before forcing a GC

private val threadPool = Executors.newFixedThreadPool(maxThreads)
private var workTodo = AtomicInteger(0)
private var workDone = AtomicInteger(0)
private var failed = AtomicInteger(0)
private var pendingWork: MutableSet<File> = ConcurrentHashMap.newKeySet()

fun main(args: Array<String>) {
  check(args.size == 1) { "usage: nbt-upgrader [worldPath]" }

  val worldPath = File(args[0])
  check(worldPath.exists()) { "world path $worldPath does not exist!" }

  val playerDir = worldPath.resolve("players")
  if (playerDir.exists()) {
    findNbtFiles(worldPath.resolve("players"))
      .forEach(::processPlayerFile)
  } else {
    println("no players directory, skipping players")
  }

  processRegion(worldPath)
  worldPath.listFiles()
    ?.filter { it.isDirectory }
    ?.filter { it.name.startsWith("DIM") }
    ?.forEach { processRegion(it) }

  threadPool.shutdown()
  threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)

  println("done!")
}

fun processRegion(basePath: File) {
  println("processing region ${basePath.name}")

  val regionDir = File(basePath, "region")
  val work = findRegionFiles(regionDir) ?: run {
    println("no region files found in ${regionDir.absolutePath}")
    return
  }

  println("found ${work.size} region files")
  workTodo.addAndGet(work.size)
  pendingWork.addAll(work)

  work.forEach {
    threadPool.submit {
      try {
        processRegionFile(it)
      } catch (e: Exception) {
        println("error processing region file $it: $e")
        failed.incrementAndGet()
      } finally {
        pendingWork.remove(it)

        if (workDone.incrementAndGet() % manualGcThreshold == 0) {
          val gcTime = measureNanoTime { System.gc() }
          if (gcTime > 1_000_000) println("GC took ${gcTime / 1_000_000}ms")
        }
      }
    }
  }
}

fun processPlayerFile(file: File) {
  println("processing player ${file.name}")

  try {
    processPlayerDat(file)
  } catch (e: Exception) {
    println("error processing player file $file: $e")
  }
}
