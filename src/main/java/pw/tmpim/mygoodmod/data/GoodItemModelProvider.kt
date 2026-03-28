package pw.tmpim.mygoodmod.data

import emmathemartian.datagen.DataGenContext
import emmathemartian.datagen.builder.LangBuilder
import emmathemartian.datagen.provider.BlockModelProvider
import emmathemartian.datagen.provider.ItemModelProvider
import emmathemartian.datagen.provider.LanguageProvider
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint
import net.modificationstation.stationapi.api.util.Namespace.MINECRAFT
import pw.tmpim.mygoodmod.MyGoodMod
import pw.tmpim.mygoodmod.MyGoodMod.MOD_ID
import pw.tmpim.mygoodmod.MyGoodMod.MOD_NAME

class GoodItemModelProvider(ctx: DataGenContext) : ItemModelProvider(ctx) {
  override fun run(ctx: DataGenContext) {
    blockItem(MyGoodMod.redstoneBlock).save("redstone_block", this, ctx)
  }
}
