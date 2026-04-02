package pw.tmpim.goodassetfetcher.api

import net.mine_diver.unsafeevents.Event
import net.modificationstation.stationapi.api.util.Namespace

class GoodAssetFetcherRegistryEvent(
  val registry: GoodAssetFetcherRegistry
): Event(), GoodAssetFetcherRegistry {
  override fun addResourceFile(
    namespace: Namespace,
    gameVersion: String,
    sourcePath: String,
    destPath: String
  ) {
    registry.addResourceFile(namespace, gameVersion, sourcePath, destPath)
  }

  override fun addResourceFile(
    namespace: Namespace,
    gameVersion: String,
    path: String
  ) {
    registry.addResourceFile(namespace, gameVersion, path)
  }

  override fun addBlock(
    namespace: Namespace,
    gameVersion: String,
    name: String
  ) {
    registry.addBlock(namespace, gameVersion, name)
  }

  override fun addItem(
    namespace: Namespace,
    gameVersion: String,
    name: String
  ) {
    registry.addItem(namespace, gameVersion, name)
  }
}
