package pw.tmpim.goodmod.death

import net.minecraft.entity.Entity
import net.minecraft.entity.mob.*
import net.minecraft.entity.passive.WolfEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.FireballEntity
import pw.tmpim.goodmod.mixin.death.EntityAccessor

object PlayerEntityDeathHandler {
  @JvmStatic
  fun createMessage(victim: PlayerEntity, provoker: Entity?): String {
    var cause: String = findCause(victim, provoker)
    val killer: Entity?
    val blastResult = resolveBlast(victim)

    if (provoker == null && blastResult != null) {
      blastResult.apply {
        killer = first
        cause = second
      }
    } else {
      killer = provoker
    }

    val name = if (cause.endsWith(" ")) getName(killer) else ""
    return victim.name + cause + name
  }

  fun resolveBlast(victim: PlayerEntity): Pair<Entity?, String>? =
    when (val blast: ExplosionTracker.BlastSource? = victim.goodmod_getBlastSource()) {
      is ExplosionTracker.EntityBlast ->
        if (blast.entity is FireballEntity)
          blast.entity.owner to " was fireballed by "
        else
          null to " blew up"

      is ExplosionTracker.BedBlast ->
        null to " was killed by [Intentional Game Design]"

      else -> null
    }

  fun findCause(victim: PlayerEntity, killer: Entity?): String {
    val drowning = victim.air <= 0
    val lit = victim.fireTicks > 0 || victim.goodmod_isLit()

    return when {
      victim.goodmod_isShot()    -> if (killer == null) " was shot by a Dispenser" else " was shot by "
      killer is CreeperEntity    -> " was blown up by "
      killer is WolfEntity       -> " was mauled to bits by "
      killer != null             -> " was slain by "
      victim.goodmod_isPricked() -> " was pricked to death"
      victim.goodmod_isStruck()  -> " was struck by lightning"
      drowning && lit            -> " drowned to a crisp"
      drowning                   -> " drowned"
      lit                        -> " burned to a crisp"
      victim.isInsideWall        -> " suffocated in a wall"
      (victim as EntityAccessor).fallDistance - 3.0f > 0
        -> " hit the ground too hard"
      victim.y < 0               -> " fell out of the world"
      else                       -> " died"
    }
  }

  fun getName(entity: Entity?): String = when (entity) {
    is PlayerEntity    -> entity.name
    is FireballEntity  -> if (entity.owner == null) "Fireball" else getName(entity.owner)
    is PigZombieEntity -> "Zombie Pigman"
    is ZombieEntity    -> "Zombie"
    is SkeletonEntity  -> "Skeleton"
    is SpiderEntity    -> "Spider"
    is CreeperEntity   -> "Creeper"
    is GhastEntity     -> "Ghast"
    is SlimeEntity     -> "Slime"
    is WolfEntity      -> if (entity.isTamed) entity.ownerName + "'s Wolf" else "Wolf"
    else               -> "Unknown"
  }
}
