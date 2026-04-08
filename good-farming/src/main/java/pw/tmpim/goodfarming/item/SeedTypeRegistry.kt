package pw.tmpim.goodfarming.item

import com.google.gson.GsonBuilder
import net.minecraft.item.ItemStack
import net.modificationstation.stationapi.api.resource.IdentifiableResourceReloadListener
import net.modificationstation.stationapi.api.resource.ResourceFinder
import net.modificationstation.stationapi.api.resource.ResourceManager
import net.modificationstation.stationapi.api.resource.ResourceReloader
import net.modificationstation.stationapi.api.tag.TagManagerLoader
import net.modificationstation.stationapi.api.util.Identifier
import net.modificationstation.stationapi.api.util.JsonHelper
import net.modificationstation.stationapi.api.util.Util
import net.modificationstation.stationapi.api.util.profiler.Profiler
import pw.tmpim.goodfarming.GoodFarming.log
import pw.tmpim.goodfarming.GoodFarming.namespace
import pw.tmpim.goodfarming.data.JsonSeedType
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor

object SeedTypeRegistry : IdentifiableResourceReloadListener {
  val registryId = namespace.id("seed_types")

  private val resourceFinder = ResourceFinder.json("$namespace/${registryId.path}")
  private val gson = GsonBuilder().create()

  private var registry: Map<Identifier, SeedType> = emptyMap()
  val entries
    get() = registry.entries

  fun get(id: Identifier): SeedType? =
    registry[id]

  fun isSeedValid(stack: ItemStack) =
    registry.values.any { it.matches(stack) }

  fun getSeedType(stack: ItemStack) =
    registry.values.find { it.matches(stack) }

  override fun getId() = registryId
  override fun getDependencies() = setOf(TagManagerLoader.TAGS)

  override fun reload(
    synchronizer: ResourceReloader.Synchronizer,
    manager: ResourceManager,
    prepareProfiler: Profiler,
    applyProfiler: Profiler,
    prepareExecutor: Executor,
    applyExecutor: Executor
  ): CompletableFuture<Void> {
    // work around a bug with gson/mixin/unsafeevents classloading where gson disappears from the classpath during
    // resource loading if we don't pre-initialise the type adapter first
    // only the first resource loader (CropTypeRegistry) actually needs to do this, but to be safe let's do it in all
    // of them
    // TODO: solve and remove this
    gson.getAdapter(JsonSeedType::class.java)

    return reloadSeedTypes(manager, prepareExecutor) // parse json
      .thenCompose(synchronizer::whenPrepared) // wait for other resources
      .thenAcceptAsync({ resources ->
        // construct the new registry
        val newRegistry = mutableMapOf<Identifier, SeedType>()
        resources.forEach { (id, json) -> loadSeedType(id, json, newRegistry) }
        registry = newRegistry
      }, applyExecutor)
  }

  private fun reloadSeedTypes(
    manager: ResourceManager,
    executor: Executor
  ): CompletableFuture<Map<Identifier, JsonSeedType>> = CompletableFuture
    .supplyAsync({ resourceFinder.findResources(manager) }, executor)
    .thenCompose { resources ->
      // Map<Identifier, Resource> -> List<CompletableFuture<Pair<Identifier, JsonSeedType>>?
      val work = resources.map { (id, res) ->
        // try to parse the json file
        CompletableFuture.supplyAsync {
          try {
            res.reader.use { reader ->
              id to JsonHelper.deserialize(gson, reader, JsonSeedType::class.java)!!
            }
          } catch (e: Exception) {
            log.error("failed to load seed type {}", id, e)
            null
          }
        }
      }

      Util.combineSafe(work)
        .thenApply { types -> types.filterNotNull().toMap() }
    }

  private fun buildSeedType(id: Identifier, json: JsonSeedType) = SeedType(
    id = id,
    item = checkNotNull(json.item) { "item missing from seed type" }.get(),
    textureId = json.textureId?.let { Identifier.of(it) },
    plantOnBlocks = json.plantOnBlocks
      ?.takeIf { it.isNotEmpty() }
      ?.map { it.get() }
  )

  private fun loadSeedType(
    id: Identifier,
    json: JsonSeedType,
    newRegistry: MutableMap<Identifier, SeedType>
  ) {
    try {
      val seedType = buildSeedType(id, json)
      newRegistry[id] = seedType
      log.debug("successfully registered seed type {}: {}", id, seedType)
    } catch (e: Exception) {
      log.error("failed to register seed type {}", id, e)
    }
  }
}
