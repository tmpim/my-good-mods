package pw.tmpim.goodmod.assets

import net.modificationstation.stationapi.api.resource.ResourceType
import net.modificationstation.stationapi.impl.resource.ResourcePackProfile
import net.modificationstation.stationapi.impl.resource.ResourcePackProvider
import net.modificationstation.stationapi.impl.resource.ResourcePackSource
import pw.tmpim.goodmod.GoodMod.MOD_ID
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
    val goodMcMeta = ResourcePackProfile.Metadata("My rather fantastic resource pack", 6)
  }
}
