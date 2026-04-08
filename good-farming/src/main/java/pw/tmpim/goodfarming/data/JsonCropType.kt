package pw.tmpim.goodfarming.data

import pw.tmpim.goodutils.data.JsonBlockKey
import pw.tmpim.goodutils.data.JsonItemKey

data class JsonCropType(
  /**
   * list of blocks that this crop type behaviour applies to.
   */
  val crops: List<JsonBlockKey>,

  /**
   * list of seeds that can replant this crop type.
   */
  val seeds: List<JsonItemKey>,
)
