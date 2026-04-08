package pw.tmpim.goodfarming.block

import net.modificationstation.stationapi.api.util.Identifier
import pw.tmpim.goodutils.block.BlockOrTag
import pw.tmpim.goodutils.item.ItemOrTag

data class CropType(
  val id: Identifier,
  val crops: List<BlockOrTag>,
  val seeds: List<ItemOrTag>,
) {

}
