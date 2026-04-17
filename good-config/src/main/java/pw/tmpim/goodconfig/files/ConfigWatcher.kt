package pw.tmpim.goodconfig.files

import com.google.common.util.concurrent.ThreadFactoryBuilder
import pw.tmpim.goodconfig.GoodConfig.log
import pw.tmpim.goodconfig.api.ConfigSpec
import java.lang.System.getProperty
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds.ENTRY_CREATE
import java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY
import java.nio.file.WatchService
import java.time.Duration
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors

object ConfigWatcher {
  private val watchedSpecs = ConcurrentHashMap<Path, ConfigSpec>()

  private val watchService: WatchService = FileSystems.getDefault().newWatchService()
  private val executor = Executors.newSingleThreadExecutor(ThreadFactoryBuilder()
    .setNameFormat("good-config-TomlWatcher-%d")
    .setDaemon(true)
    .build())

  private val queuedReloads = ConcurrentHashMap<ConfigSpec, Instant>()

  private const val P = "good-config.reload"
  private val reloadEnabled     = getProperty("$P.disabled", "false") == "true"
  private val reloadDebounce    = Duration.ofMillis(getProperty("$P.debounceMs", "100").toLong().coerceAtLeast(0))
  private val reloadQueueFrames = getProperty("$P.queueFrames", "10").toLong().coerceAtLeast(1)
  private val reloadQueueTicks  = getProperty("$P.queueTicks", "5").toLong().coerceAtLeast(1)

  private var ticks = 0L

  fun start() {
    if (!reloadEnabled) return

    log.info("starting config watcher")

    executor.submit {
      try {
        while (!Thread.currentThread().isInterrupted) {
          val key = watchService.take() // blocks until event

          for (event in key.pollEvents()) {
            val context = event.context() as? Path
            val filename = context?.fileName ?: continue
            val spec = watchedSpecs[filename] ?: continue

            queuedReloads[spec] = Instant.now() // bump the debounced time up
          }

          key.reset()
        }
      } catch (_: InterruptedException) {
        log.info("config watcher stopped")
      }
    }
  }

  fun stop() {
    log.info("shutting down config watcher")
    queuedReloads.clear()
    executor.shutdownNow()
    watchService.close()
  }

  fun startWatching(file: Path, spec: ConfigSpec) {
    if (!reloadEnabled) return

    // watch the parent directory of the config file, so we can catch atomic file replacements
    watchedSpecs[file] = spec
    file.parent.register(watchService, ENTRY_CREATE, ENTRY_MODIFY)
  }

  @JvmStatic
  fun shouldTickClient() = reloadEnabled && ticks++ >= reloadQueueFrames

  @JvmStatic
  fun shouldTickServer() = reloadEnabled && ticks++ >= reloadQueueTicks

  // called every 10 frames (vanilla) or every 5 ticks (server)
  @JvmStatic
  fun processQueuedReloads() {
    ticks = 0L

    queuedReloads.entries.removeIf { (spec, modifyTime) ->
      if (Instant.now().minus(reloadDebounce).isAfter(modifyTime)) {
        return@removeIf false
      }

      val file = spec.file

      try {
        log.info("reloading config $file")
        spec.holder?.loadFromFile(file)
        log.info("successfully reloaded config $file")
      } catch (e: Exception) {
        log.error("failed to reload config $file", e)
      }

      true // pop from the queue regardless, so we don't spam with errors
    }
  }
}
