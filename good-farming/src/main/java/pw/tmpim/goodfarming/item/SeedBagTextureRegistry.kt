package pw.tmpim.goodfarming.item

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.modificationstation.stationapi.api.client.texture.atlas.Atlas
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases
import pw.tmpim.goodfarming.GoodFarming.namespace

@Environment(EnvType.CLIENT)
object SeedBagTextureRegistry {
  private val itemAtlas by lazy { Atlases.getGuiItems() }

  private val baseTextureId = namespace.id("item/seed_bag")
  internal val baseTexture by lazy { itemAtlas.addTexture(baseTextureId) }

  private val textureCache = mutableMapOf<SeedType, Atlas.Sprite?>()

  @Environment(EnvType.CLIENT)
  fun getBagTexture(type: SeedType): Atlas.Sprite =
    textureCache.computeIfAbsent(type) {
      type.textureId?.let { itemAtlas.addTexture(it) }
    } ?: baseTexture
}
