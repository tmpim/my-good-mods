package pw.tmpim.goodfarming.data

import net.modificationstation.stationapi.api.item.json.JsonItemKey

data class JsonSeedType(
  /**
   * the item for this seed type. affects the crafting recipe and placement
   */
  val item: JsonItemKey? = null,

  /**
   * texture to render the bag with when this seed type is inserted. if absent, the bag's base texture will be used
   */
  val textureName: String? = null,

  /**
   * list of blocks the seed will attempt to be planted on (e.g. farmland). if absent, it will try to plant on all
   * blocks (assuming Item.useOnBlock allows it)
   */
  val plantOnBlocks: List<JsonItemKey>? = null,
)
