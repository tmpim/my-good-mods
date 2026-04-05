package pw.tmpim.gooddeathmessages

import net.minecraft.entity.projectile.ArrowEntity
import pw.tmpim.gooddeathmessages.ExplosionTracker.BlastSource

@Suppress("PropertyName")
interface Victim {
  // Shot by arrow
  var `gooddms$shotBy`: ArrowEntity?
  // Pricked by cactus
  var `gooddms$pricked`: Boolean
  // Damaged by an explosion
  var `gooddms$blastSource`: BlastSource?
  // Struck by lightning
  var `gooddms$struck`: Boolean
  // Taking damage from being *in* a fire. Fire ticks are a separate check.
  var `gooddms$lit`: Boolean
}
