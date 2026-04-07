package pw.tmpim.goodfarming.api

import emmathemartian.datagen.AbstractDataProvider
import emmathemartian.datagen.DataGenContext
import emmathemartian.datagen.DataTarget
import pw.tmpim.goodfarming.item.SeedTypeRegistry.registryId

abstract class SeedTypeProvider(
  ctx: DataGenContext
) : AbstractDataProvider(
  "../${registryId.namespace}/${registryId.path}", // get out of /stationapi
  "Seed Types",
  DataTarget.DATA,
  ctx
) {
  protected fun seedType() = SeedTypeBuilder()
}
