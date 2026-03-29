package pw.tmpim.mygoodmod.data

import net.minecraft.block.Block

object GoodPatches {
  @JvmStatic
  val pickaxeEffective: List<Block> = mutableListOf<Block>().apply {
    add(Block.REDSTONE_ORE)
    add(Block.LIT_REDSTONE_ORE)
    add(Block.FURNACE)
    add(Block.LIT_FURNACE)
    add(Block.COBBLESTONE_STAIRS)
    add(Block.CRAFTING_TABLE)
    add(Block.SPAWNER)
  }

  @JvmStatic
  val axeEffective: List<Block> = mutableListOf<Block>().apply {
    add(Block.LOCKED_CHEST)
    add(Block.TRAPDOOR)
    add(Block.WOODEN_STAIRS)
    add(Block.PUMPKIN)
    add(Block.JACK_O_LANTERN)
    add(Block.FENCE)
    add(Block.JUKEBOX)
  }

  @JvmStatic
  val shovelEffective: List<Block> = mutableListOf<Block>().apply {
    add(Block.SOUL_SAND)
  }
}
