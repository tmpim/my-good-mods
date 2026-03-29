package pw.tmpim.mygoodmod.assets

import pw.tmpim.mygoodmod.MyGoodMod.namespace

object GoodResources {
  private val _files = mutableSetOf<ResourceDef>()
  val files: Set<ResourceDef> = _files // make immutable

  private val destPathFileMap = mutableMapOf<String, ResourceDef>()

  fun addResourceFile(version: String, sourcePath: String, destPath: String) {
    val def = ResourceDef(version, sourcePath, destPath)

    // prevent duplicate dest paths
    check(destPathFileMap.put(destPath, def) == null) { "$destPath already exists in GoodResources" }

    _files.add(def)
  }

  fun addResourceFile(version: String, path: String) {
    addResourceFile(version, path, path)
  }

  fun addBlock(version: String, name: String) {
    addResourceFile(version, "assets/minecraft/textures/blocks/$name.png")
  }

  data class ResourceDef(
    val version: String,
    val sourcePath: String,
    var destPath: String,
  ) {
    var fetched = false

    // apply stationapi-compatible replacements to the destination path
    init {
      destPath = destPath.replace("assets/minecraft/textures/blocks", "assets/${namespace}/stationapi/textures/block")
    }
  }
}
