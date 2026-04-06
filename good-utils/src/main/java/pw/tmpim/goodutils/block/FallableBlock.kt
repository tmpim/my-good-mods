package pw.tmpim.goodutils.block

import net.minecraft.entity.Entity
import net.minecraft.world.World

@Suppress("FunctionName")
interface FallableBlock {
  fun `goodutils$fallOn`(world: World, x: Int, y: Int, z: Int, entity: Entity, fallDistance: Double) {}
}
