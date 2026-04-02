package pw.tmpim.goodassetfetcher

import net.modificationstation.stationapi.api.resource.ResourceType
import net.modificationstation.stationapi.impl.resource.ResourcePackProfile
import net.modificationstation.stationapi.impl.resource.ResourcePackProvider
import net.modificationstation.stationapi.impl.resource.ResourcePackSource
import pw.tmpim.goodassetfetcher.GoodAssetFetcher.MOD_ID
import pw.tmpim.goodassetfetcher.GoodAssetFetcher.PACK_NAME
import java.util.function.Consumer

class GoodResourcePackProvider : ResourcePackProvider {
  override fun register(profileAdder: Consumer<ResourcePackProfile>) {
    val pack = GoodResourcePack()
    profileAdder.accept(ResourcePackProfile.of(
      "${MOD_ID}_generated",
      pack.name,
      true,
      { pack },
      goodMcMeta,
      ResourceType.CLIENT_RESOURCES,
      ResourcePackProfile.InsertionPosition.TOP,
      true,
      ResourcePackSource.BUILTIN
    ))
  }

  companion object {
    val goodMcMeta = ResourcePackProfile.Metadata(PACK_NAME, 6)
  }
}
