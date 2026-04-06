package pw.tmpim.goodfarming.item

import net.modificationstation.stationapi.api.client.texture.atlas.Atlases
import pw.tmpim.goodfarming.GoodFarming.namespace

// TODO: a purely data-driven way to do this would be nice
object SeedBagItemTextures {
  private val itemAtlas = Atlases.getGuiItems()

  val base       = itemAtlas.addTexture(namespace.id("item/seed_bag"))
  val wheatSeeds = itemAtlas.addTexture(namespace.id("item/seed_bag_wheat_seeds"))
  val boneMeal   = itemAtlas.addTexture(namespace.id("item/seed_bag_bone_meal"))
}
