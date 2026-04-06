package pw.tmpim.gooddeathmessages

import net.minecraft.entity.projectile.ArrowEntity
import pw.tmpim.gooddeathmessages.ExplosionTracker.BlastSource

@Suppress("PropertyName")
interface Victim {

  val `gooddms$victim`: Data

  data class Data(
    // Shot by arrow
    var shotBy: ArrowEntity? = null,
    // Pricked by cactus
    var pricked: Boolean = false,
    // Damaged by an explosion
    var blastSource: BlastSource? = null,
    // Struck by lightning
    var struck: Boolean = false,
    // Taking damage from being *in* a fire. Fire ticks are a separate check.
    var lit: Boolean = false,
    // Taking damage from being in lava.
    var lava: Boolean = false,
    // true if player was on a ladder, remains true until on ground.
    var wasClimbing: Boolean = false,
    // /kill
    var killCommand: Boolean = false,
    // Projectile (ex. snowball)
    var projectile: Boolean = false
  ) {
  }
}
