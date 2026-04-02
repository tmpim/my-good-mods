package pw.tmpim.goodcompression.data

import emmathemartian.datagen.DataGenContext
import emmathemartian.datagen.builder.LangBuilder
import emmathemartian.datagen.provider.LanguageProvider
import pw.tmpim.goodcompression.CONFIG_KEY
import pw.tmpim.goodcompression.GoodCompression
import pw.tmpim.goodcompression.GoodCompression.MOD_NAME

private const val C = CONFIG_KEY

class GoodCompressionLanguageProvider(ctx: DataGenContext) : LanguageProvider(ctx) {
  override fun run(ctx: DataGenContext) {
    LangBuilder()
      .add("$C.name", MOD_NAME)
      .add(GoodCompression.redstoneBlock, "Block of Redstone")
      .add(GoodCompression.stoneBricksBlock, "Stone Bricks")
      .add(GoodCompression.hayBlock, "Hay Bale")
      .add(GoodCompression.coalBlock, "Block of Coal")
      .add("$C.redstone_dust_on_top_of_blocks", "Redstone dust on top of blocks")
      .add("$C.redstone_dust_on_top_of_blocks.desc", "Allow redstone dust to be placed on non-solid blocks, such as "
        + "Redstone Blocks, if they are in the good-compression:redstone_dust_placeable tag.")
      .save("en_US", this, ctx)
  }
}
