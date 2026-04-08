package pw.tmpim.goodfarming.block

import com.google.gson.GsonBuilder
import net.modificationstation.stationapi.api.resource.IdentifiableResourceReloadListener
import net.modificationstation.stationapi.api.resource.ResourceFinder
import net.modificationstation.stationapi.api.resource.ResourceManager
import net.modificationstation.stationapi.api.resource.ResourceReloader
import net.modificationstation.stationapi.api.tag.TagManagerLoader
import net.modificationstation.stationapi.api.util.Identifier
import net.modificationstation.stationapi.api.util.JsonHelper
import net.modificationstation.stationapi.api.util.Util
import net.modificationstation.stationapi.api.util.profiler.Profiler
import pw.tmpim.goodfarming.GoodFarming
import pw.tmpim.goodfarming.GoodFarming.namespace
import pw.tmpim.goodfarming.data.JsonCropType
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor

object CropTypeRegistry : IdentifiableResourceReloadListener {
  val registryId = namespace.id("crop_types")

  private val resourceFinder = ResourceFinder.json("${namespace}/${registryId.path}")
  private val gson = GsonBuilder().create()

  private var registry: Map<Identifier, CropType> = emptyMap()
  val entries
    get() = registry.entries

  fun get(id: Identifier): CropType? =
    registry[id]

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
    return reloadCropTypes(manager, prepareExecutor) // parse json
      .thenCompose(synchronizer::whenPrepared) // wait for other resources
      .thenAcceptAsync({ resources ->
        // construct the new registry
        val newRegistry = mutableMapOf<Identifier, CropType>()
        resources.forEach { (id, json) -> loadCropType(id, json, newRegistry) }
        registry = newRegistry
      }, applyExecutor)
  }

  private fun reloadCropTypes(
    manager: ResourceManager,
    executor: Executor
  ): CompletableFuture<Map<Identifier, JsonCropType>> = CompletableFuture
    .supplyAsync({ resourceFinder.findResources(manager) }, executor)
    .thenCompose { resources ->
      // Map<Identifier, Resource> -> List<CompletableFuture<Pair<Identifier, JsonCropType>>?
      val work = resources.map { (id, res) ->
        // try to parse the json file
        CompletableFuture.supplyAsync {
          try {
            res.reader.use { reader ->
              id to JsonHelper.deserialize(gson, reader, JsonCropType::class.java)!!
            }
          } catch (e: Exception) {
            GoodFarming.log.error("failed to load crop type {}", id, e)
            null
          }
        }
      }

      Util.combineSafe(work)
        .thenApply { types -> types.filterNotNull().toMap() }
    }

  private fun buildCropType(id: Identifier, json: JsonCropType) = CropType(
    id = id,
    crops = json.crops.map { it.get() },
    seeds = json.seeds.map { it.get() },
  )

  private fun loadCropType(
    id: Identifier,
    json: JsonCropType,
    newRegistry: MutableMap<Identifier, CropType>
  ) {
    try {
      val cropType = buildCropType(id, json)
      newRegistry[id] = cropType
      GoodFarming.log.debug("successfully registered crop type {}: {}", id, cropType)
    } catch (e: Exception) {
      GoodFarming.log.error("failed to register crop type {}", id, e)
    }
  }
}
