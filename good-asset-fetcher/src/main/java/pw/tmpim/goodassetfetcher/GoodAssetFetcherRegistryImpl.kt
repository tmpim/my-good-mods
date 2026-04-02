package pw.tmpim.goodassetfetcher

import net.modificationstation.stationapi.api.util.Namespace
import pw.tmpim.goodassetfetcher.api.GoodAssetFetcherRegistry

internal object GoodAssetFetcherRegistryImpl : GoodAssetFetcherRegistry {
  private val _files = mutableSetOf<ResourceDef>()
  internal val files: Set<ResourceDef> = _files // make immutable

  private val destPathFileMap = mutableMapOf<String, ResourceDef>()

  override fun addResourceFile(namespace: Namespace, gameVersion: String, sourcePath: String, destPath: String) {
    val def = ResourceDef(namespace, gameVersion, sourcePath, destPath)

    // prevent duplicate dest paths
    check(destPathFileMap.put(destPath, def) == null) { "$destPath already exists in GoodResources" }

    _files.add(def)
  }

  override fun addResourceFile(namespace: Namespace, gameVersion: String, path: String) {
    addResourceFile(namespace, gameVersion, path, path)
  }

  override fun addBlock(namespace: Namespace, gameVersion: String, name: String) {
    addResourceFile(namespace, gameVersion, "assets/minecraft/textures/blocks/$name.png")
  }

  override fun addItem(namespace: Namespace, gameVersion: String, name: String) {
    addResourceFile(namespace, gameVersion, "assets/minecraft/textures/items/$name.png")
  }

  internal data class ResourceDef(
    val namespace: Namespace,
    val version: String,
    val sourcePath: String,
    var destPath: String,
  ) {
    var fetched = false

    init {
      applyDestPathTransformations()
    }

    fun applyDestPathTransformations() {
      // apply stationapi-compatible replacements to the destination path
      destPath = destPath.replace("assets/minecraft/textures/blocks", "assets/${namespace}/stationapi/textures/block")
      destPath = destPath.replace("assets/minecraft/textures/items", "assets/${namespace}/stationapi/textures/item")
    }
  }
}
