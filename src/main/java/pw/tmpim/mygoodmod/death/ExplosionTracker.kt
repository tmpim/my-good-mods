package pw.tmpim.mygoodmod.death

import net.minecraft.entity.Entity

object ExplosionTracker {
  private val sourceQueue = ArrayDeque<BlastSource>()

  @JvmStatic
  fun pushBlast(source: BlastSource) {
    sourceQueue.add(source)
  }

  @JvmStatic
  fun popBlast(): BlastSource? {
    return sourceQueue.removeFirstOrNull();
  }

  open class BlastSource

  class BedBlast : BlastSource()

  class EntityBlast : BlastSource {
    val entity: Entity

    constructor(entity: Entity) {
      this.entity = entity
    }
  }
}
