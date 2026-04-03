package pw.tmpim.goodsounds.data

import emmathemartian.datagen.DataGenContext
import emmathemartian.datagen.provider.ItemTagProvider
import net.minecraft.block.Block
import net.minecraft.item.Item.*

class GoodSoundsItemTagProvider(ctx: DataGenContext): ItemTagProvider(ctx) {
  override fun run(ctx: DataGenContext?) {
    tag()
      .add(IRON_AXE)
      .add(IRON_HOE)
      .add(IRON_DOOR)
      .add(IRON_BOOTS)
      .add(IRON_INGOT)
      .add(IRON_CHESTPLATE)
      .add(IRON_HELMET)
      .add(IRON_LEGGINGS)
      .add(IRON_SWORD)
      .add(IRON_PICKAXE)
      .add(IRON_SHOVEL)
      .add(CHAIN_BOOTS)
      .add(CHAIN_HELMET)
      .add(CHAIN_LEGGINGS)
      .add(CHAIN_CHESTPLATE)
      .add(BUCKET)
      .add(MILK_BUCKET)
      .add(LAVA_BUCKET)
      .add(WATER_BUCKET)
      .add(FLINT_AND_STEEL)
      .add(Block.IRON_BLOCK.asItem())
      .add(Block.IRON_DOOR.asItem())
      .save("metal_item", this, ctx)
  }
}
