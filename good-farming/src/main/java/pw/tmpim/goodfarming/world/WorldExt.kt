package pw.tmpim.goodfarming.world

import net.minecraft.item.ItemStack
import net.minecraft.world.World

@Suppress("PropertyName")
interface WorldExt {
  /**
   * when this function is set on the world, spawned item entities will be captured and passed through this predicate.
   * if it returns true, the item spawn is prevented. if it returns false, it is allowed. the item stack may be modified
   * prior to spawning.
   */
  val `goodfarming$capturingItemSpawns`: ThreadLocal<((ItemStack) -> Boolean)?>
}

val World.capturingItemSpawns
  get() = `goodfarming$capturingItemSpawns`
