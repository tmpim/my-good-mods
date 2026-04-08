package pw.tmpim.goodfarming.api

import emmathemartian.datagen.AbstractDataProvider
import emmathemartian.datagen.DataGenContext
import emmathemartian.datagen.DataTarget
import pw.tmpim.goodfarming.block.CropTypeRegistry

abstract class CropTypeProvider(
  ctx: DataGenContext
) : AbstractDataProvider(
  "../${CropTypeRegistry.registryId.namespace}/${CropTypeRegistry.registryId.path}", // get out of /stationapi
  "Crop Types",
  DataTarget.DATA,
  ctx
) {
  protected fun cropType() = CropTypeBuilder()
}
