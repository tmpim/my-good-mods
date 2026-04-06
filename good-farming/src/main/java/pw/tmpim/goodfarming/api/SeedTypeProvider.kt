package pw.tmpim.goodfarming.api

import emmathemartian.datagen.AbstractDataProvider
import emmathemartian.datagen.DataGenContext
import emmathemartian.datagen.DataTarget

abstract class SeedTypeProvider(
  ctx: DataGenContext
) : AbstractDataProvider(
  "seed-types",
  "Seed Types",
  DataTarget.DATA,
  ctx
) {
  protected fun seedType() = SeedTypeBuilder()
}
