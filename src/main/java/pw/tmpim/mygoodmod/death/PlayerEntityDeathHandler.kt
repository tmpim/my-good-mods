package pw.tmpim.mygoodmod.death

import net.minecraft.entity.Entity
import net.minecraft.entity.mob.*
import net.minecraft.entity.passive.WolfEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.FireballEntity
import pw.tmpim.mygoodmod.mixin.death.EntityAccessor

object PlayerEntityDeathHandler {
  @JvmStatic
  fun createMessage(victim: PlayerEntity, provoker: Entity?): String {
    var cause: String = findCause(victim, provoker)
    val killer: Entity?
    val blastResult = resolveBlast(victim)
    if (provoker == null && blastResult != null)  {
      blastResult.apply {
        killer = first
        cause = second
      }
    } else {
      killer = provoker
    }
    val name = (if (cause.endsWith(" ")) getName(killer) else "")
    return victim.name + cause + name
  }

  fun resolveBlast(victim: PlayerEntity): Pair<Entity?, String>? {
    val blast: ExplosionTracker.BlastSource? = (victim as Victim).goodmod_isBlasted()
    if (blast is ExplosionTracker.EntityBlast) {
      return if (blast.entity is FireballEntity) {
        Pair(blast.entity.owner, " was fireballed by ")
      } else {
        Pair(null, " blew up")
      }
    } else if (blast is ExplosionTracker.BedBlast) {
      return Pair(null, " was killed by [Intentional Game Design]")
    }
    return null
  }

  fun findCause(victim: PlayerEntity, killer: Entity?): String {
    val drowning = victim.air <= 0
    val lit = victim.fireTicks > 0 || (victim as Victim).goodmod_isLit()
    if (killer is CreeperEntity) {
      return " was blown up by "
    } else if ((victim as Victim).goodmod_isShot()) {
      if (killer == null) return " was shot by a Dispenser"
      return " was shot by "
    } else if ((victim as Victim).goodmod_isPricked()) {
      return " was pricked to death"
    } else if (killer is WolfEntity) {
      return " was mauled to bits by "
    } else if ((victim as Victim).goodmod_isStruck()) {
      return " was struck by lightning"
    } else if (killer != null) {
      return " was slain by "
    } else if (drowning && lit) {
      return " drowned to a crisp"
    } else if (drowning) {
      return " drowned"
    } else if (lit) {
      return " burned to a crisp"
    } else if (victim.isInsideWall) {
      return " suffocated in a wall"
    } else if ((victim as EntityAccessor).fallDistance - 3.0f > 0) {
      return " hit the ground too hard"
    } else if (victim.y < 0) {
      return " fell out of the world"
    } else {
      return " died"
    }
  }

  fun getName(entity: Entity?): String {
    return when (entity) {
      is PlayerEntity -> entity.name
      is FireballEntity -> if (entity.owner == null) "Fireball" else getName(entity.owner)
      is PigZombieEntity -> "Zombie Pigman"
      is ZombieEntity -> "Zombie"
      is SkeletonEntity -> "Skeleton"
      is SpiderEntity -> "Spider"
      is CreeperEntity -> "Creeper"
      is GhastEntity -> "Ghast"
      is SlimeEntity -> "Slime"
      is WolfEntity -> if (entity.isTamed) entity.ownerName + "'s Wolf" else "Wolf"
      else -> "Unknown"
    }
  }
}
