package pw.tmpim.goodmod.assets

import com.google.gson.GsonBuilder
import net.modificationstation.stationapi.api.resource.InputSupplier
import net.modificationstation.stationapi.api.resource.ResourceType
import net.modificationstation.stationapi.api.util.Identifier
import net.modificationstation.stationapi.impl.resource.DirectoryResourcePack
import pw.tmpim.goodmod.GoodMod.MOD_NAME
import pw.tmpim.goodmod.assets.GoodResourcePackProvider.Companion.goodMcMeta
import java.io.InputStream
import kotlin.io.path.exists
import kotlin.io.path.writeText

class GoodResourcePack : DirectoryResourcePack(
  "$MOD_NAME Generated",
  AssetFetcher.cacheDir,
  true /* TODO */
) {
  private val gson = GsonBuilder().create()

  override fun openRoot(vararg segments: String): InputSupplier<InputStream>? {
    // ensure the assets exist on disk before loading the resource pack
    AssetFetcher.fetchAssets()

    // ensure pack.mcmeta exists
    val mcmetaFile = AssetFetcher.cacheDir.resolve("pack.mcmeta")
    if (!mcmetaFile.exists()) {
      val metadata = McMetaFile(McMetaFile.Metadata(goodMcMeta.description, goodMcMeta.format))
      mcmetaFile.writeText(gson.toJson(metadata))
    }

    // delegate the rest to DirectoryResourcePack with our cache dir
    return super.openRoot(*segments)
  }

  override fun open(
    type: ResourceType?,
    id: Identifier?
  ): InputSupplier<InputStream?>? {
    return super.open(type, id)
  }

  data class McMetaFile(
    val pack: Metadata
  ) {
    data class Metadata(
      val description: String,
      val pack_format: Int
    )
  }
}
