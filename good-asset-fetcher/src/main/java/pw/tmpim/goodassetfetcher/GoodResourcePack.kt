package pw.tmpim.goodassetfetcher

import com.google.gson.GsonBuilder
import net.modificationstation.stationapi.api.resource.InputSupplier
import net.modificationstation.stationapi.api.resource.ResourceType
import net.modificationstation.stationapi.api.util.Identifier
import net.modificationstation.stationapi.impl.resource.DirectoryResourcePack
import pw.tmpim.goodassetfetcher.GoodAssetFetcher.PACK_NAME
import pw.tmpim.goodassetfetcher.GoodAssetFetcher.log
import pw.tmpim.goodassetfetcher.GoodResourcePackProvider.Companion.goodMcMeta
import java.io.InputStream
import kotlin.io.path.exists
import kotlin.io.path.writeText

internal class GoodResourcePack : DirectoryResourcePack(
  PACK_NAME,
  AssetFetcherImpl.cacheDir,
  true /* TODO */
) {
  private val gson = GsonBuilder().create()

  override fun openRoot(vararg segments: String): InputSupplier<InputStream>? {
    try {
      // ensure the assets exist on disk before loading the resource pack
      AssetFetcherImpl.fetchAssets()

      // ensure pack.mcmeta exists
      val mcmetaFile = AssetFetcherImpl.cacheDir.resolve("pack.mcmeta")
      if (!mcmetaFile.exists()) {
        val metadata = McMetaFile(McMetaFile.Metadata(goodMcMeta.description, goodMcMeta.format))
        mcmetaFile.writeText(gson.toJson(metadata))
      }
    } catch (e: Exception) {
      log.error("error preparing assets, some assets may be missing", e)
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

  private data class McMetaFile(
    val pack: Metadata
  ) {
    data class Metadata(
      val description: String,
      val pack_format: Int
    )
  }
}
