package pw.tmpim.mygoodmod.assets

import net.modificationstation.stationapi.api.resource.InputSupplier
import net.modificationstation.stationapi.impl.resource.DirectoryResourcePack
import pw.tmpim.mygoodmod.MyGoodMod.MOD_NAME
import java.io.InputStream

class GoodResourcePack : DirectoryResourcePack(
  "$MOD_NAME Generated",
  AssetFetcher.cacheDir,
  true /* TODO */
) {
  override fun openRoot(vararg segments: String): InputSupplier<InputStream>? {
    // ensure the assets exist on disk before loading the resource pack
    AssetFetcher.fetchAssets()

    // delegate the rest to DirectoryResourcePack with our cache dir
    return super.openRoot(*segments)
  }
}
