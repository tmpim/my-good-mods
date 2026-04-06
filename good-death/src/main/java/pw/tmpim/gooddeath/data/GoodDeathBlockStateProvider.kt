package pw.tmpim.gooddeath.data

import emmathemartian.datagen.DataGenContext
import emmathemartian.datagen.builder.VariantStateBuilder.Variant
import emmathemartian.datagen.provider.BlockStateProvider
import net.modificationstation.stationapi.api.util.math.Direction.*
import pw.tmpim.gooddeath.GoodDeath.namespace
import pw.tmpim.gooddeath.block.TombstoneBlock.Companion.FACING

class GoodDeathBlockStateProvider(ctx: DataGenContext) : BlockStateProvider(ctx) {
  override fun run(ctx: DataGenContext?) {
    val model = namespace.id("block/tombstone")
    variant()
      .variant(FACING, EAST, Variant(model, 0, 0, false, 0))
      .variant(FACING, SOUTH, Variant(model, 0, 90, false, 0))
      .variant(FACING, WEST, Variant(model, 0, 180, false, 0))
      .variant(FACING, NORTH, Variant(model, 0, 270, false, 0))
      .save("tombstone", this, ctx)
  }
}
