package pw.tmpim.gooddeathmessages

import net.minecraft.client.resource.language.TranslationStorage
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.MobEntity
import net.minecraft.entity.passive.WolfEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.FireballEntity
import pw.tmpim.gooddeathmessages.data.GoodDeathMessagesData.namespace
import pw.tmpim.gooddeathmessages.mixin.EntityAccessor

object DeathHooks {

  val CACTUS = object : Cause("${namespace}.death.attack.cactus") {
    override fun test(victim: PlayerEntity, killer: Entity?): Boolean = victim.`gooddms$victim`.pricked
  }

  val DROWN = object : Cause("${namespace}.death.attack.drown") {
    override fun test(victim: PlayerEntity, killer: Entity?): Boolean = victim.air <= 0
  }

  // Generic Explosion
  val EXPLOSION = object : Cause("${namespace}.death.attack.explosion") {
    override fun test(victim: PlayerEntity, killer: Entity?): Boolean =
      victim.`gooddms$victim`.blastSource is ExplosionTracker.EntityBlast
  }

  // Explosion associated to an entity
  val EXPLOSION_PLAYER = object : Cause("${namespace}.death.attack.explosion.player") {
    override fun test(victim: PlayerEntity, killer: Entity?): Boolean =
      victim.`gooddms$victim`.blastSource is ExplosionTracker.EntityBlast &&
        (victim.`gooddms$victim`.blastSource as ExplosionTracker.EntityBlast).entity is LivingEntity

    override fun populate(victim: PlayerEntity, killer: Entity?): MutableList<String> {
      return super.populate(victim, killer).apply {
        addLast(DeathRegistry.getName((victim.`gooddms$victim`.blastSource as ExplosionTracker.EntityBlast).entity))
      }
    }
  }

  // Explosion associated to a bed
  val EXPLOSION_BED = object : Cause("${namespace}.death.attack.badRespawnPoint.message") {
    override fun test(victim: PlayerEntity, killer: Entity?): Boolean =
      victim.`gooddms$victim`.blastSource is ExplosionTracker.BedBlast
  }

  // A fall of less than 5 blocks
  val FALL = object : Cause("${namespace}.death.attack.fall") {
    override fun test(victim: PlayerEntity, killer: Entity?): Boolean =
      (victim as EntityAccessor).fallDistance - 3.0f > 0
  }

  // A fall of more than 5 blocks
  val FALL_FAR = object : Cause("${namespace}.death.fell.accident.generic") {
    override fun test(victim: PlayerEntity, killer: Entity?): Boolean =
      (victim as EntityAccessor).fallDistance - 5.0f > 0
  }

  val FALL_LADDER = object : Cause("${namespace}.death.fell.accident.ladder") {
    override fun test(victim: PlayerEntity, killer: Entity?): Boolean =
      (victim as EntityAccessor).fallDistance - 3.0f > 0 && victim.`gooddms$victim`.wasClimbing
  }

  val FALL_WATER = object : Cause("${namespace}.death.fell.accident.water") {
    override fun test(victim: PlayerEntity, killer: Entity?): Boolean {
      val access: EntityAccessor = victim as EntityAccessor
      return access.fallDistance - 3.0f > 0 && victim.checkWaterCollisions()
    }
  }

  val IN_FIRE = object : Cause("${namespace}.death.attack.inFire") {
    override fun test(victim: PlayerEntity, killer: Entity?): Boolean = victim.`gooddms$victim`.lit
  }

  val ON_FIRE = object : Cause("${namespace}.death.attack.onFire") {
    override fun test(victim: PlayerEntity, killer: Entity?): Boolean = victim.fireTicks > 0
  }

  val LAVA = object : Cause("${namespace}.death.attack.lava") {
    override fun test(victim: PlayerEntity, killer: Entity?): Boolean = victim.`gooddms$victim`.lava
  }

  val LIGHTNING = object : Cause("${namespace}.death.attack.lightningBolt") {
    override fun test(victim: PlayerEntity, killer: Entity?): Boolean = victim.`gooddms$victim`.struck
  }

  val PLAYER = object : KillerWithCause("${namespace}.death.attack.player") {
    override fun test(victim: PlayerEntity, killer: Entity?): Boolean = killer is PlayerEntity
  }

  val MOB = object : KillerWithCause("${namespace}.death.attack.mob") {
    override fun test(victim: PlayerEntity, killer: Entity?): Boolean = killer is MobEntity
  }

  val ARROW = object : Cause("${namespace}.death.attack.arrow") {
    override fun test(victim: PlayerEntity, killer: Entity?): Boolean = victim.`gooddms$victim`.shotBy != null

    override fun populate(victim: PlayerEntity, killer: Entity?): MutableList<String> {
      return super.populate(victim, killer).apply {
        addLast(DeathRegistry.getName(killer ?: victim.`gooddms$victim`.shotBy))
      }
    }
  }

  val THROWN = object : Cause("${namespace}.death.attack.thrown") {
    override fun test(victim: PlayerEntity, killer: Entity?): Boolean = victim.`gooddms$victim`.projectile
  }

  val FIREBALL = object : Cause("${namespace}.death.attack.fireball") {
    override fun test(victim: PlayerEntity, killer: Entity?): Boolean {
      val blast: ExplosionTracker.BlastSource? = victim.`gooddms$victim`.blastSource
      return blast is ExplosionTracker.EntityBlast && blast.entity is FireballEntity
    }

    override fun populate(victim: PlayerEntity, killer: Entity?): MutableList<String> {
      val blast: ExplosionTracker.EntityBlast = victim.`gooddms$victim`.blastSource as ExplosionTracker.EntityBlast
      val owner: Entity = (blast.entity as FireballEntity).owner
      return super.populate(victim, killer).apply {
        addLast(DeathRegistry.getName(owner))
      }
    }
  }

  val IN_WALL = object : Cause("${namespace}.death.attack.inWall") {
    override fun test(victim: PlayerEntity, killer: Entity?): Boolean = victim.isInsideWall
  }

  val OUT_OF_WORLD = object : Cause("${namespace}.death.attack.outOfWorld") {
    override fun test(victim: PlayerEntity, killer: Entity?): Boolean = victim.y < -64.0F
  }

  val GENERIC = object : Cause("${namespace}.death.attack.generic") {
    override fun test(victim: PlayerEntity, killer: Entity?): Boolean = true
  }

  val GENERIC_KILL = object : Cause("${namespace}.death.attack.genericKill") {
    override fun test(victim: PlayerEntity, killer: Entity?): Boolean = victim.`gooddms$victim`.killCommand
  }

  // Custom

  val WOLF = object : KillerWithCause("${namespace}.death.attack.wolf") {
    override fun test(victim: PlayerEntity, killer: Entity?): Boolean = killer is WolfEntity
  }

  val WOLF_PLAYER = object : Cause("${namespace}.death.attack.wolf.player") {
    override fun test(victim: PlayerEntity, killer: Entity?): Boolean = killer is WolfEntity && killer.isTamed

    override fun populate(victim: PlayerEntity, killer: Entity?): MutableList<String> {
      return super.populate(victim, killer).apply {
        addLast((killer as WolfEntity).ownerName) // user's
        addLast(DeathRegistry.getName(killer)) // wolf
      }
    }
  }

  val DROWN_AND_BURN = object : Cause("${namespace}.death.attack.drownBurn") {
    override fun test(victim: PlayerEntity, killer: Entity?): Boolean =
      victim.air <= 0 && (victim.fireTicks > 0 || victim.`gooddms$victim`.lit)
  }

  // Cause Class

  val translations: TranslationStorage = TranslationStorage.getInstance()

  abstract class KillerWithCause(translationKey: String) : Cause(translationKey) {
    override fun populate(victim: PlayerEntity, killer: Entity?): MutableList<String> {
      return super.populate(victim, killer).apply {
        addLast(DeathRegistry.getName(killer))
      }
    }
  }

  abstract class Cause {
    val translationKey: String

    constructor(translationKey: String) {
      this.translationKey = translationKey
    }

    abstract fun test(victim: PlayerEntity, killer: Entity?): Boolean

    open fun populate(victim: PlayerEntity, killer: Entity?): MutableList<String> {
      return mutableListOf(victim.name)
    }

    fun translate(victim: PlayerEntity, killer: Entity?): String {
      return translations.get(translationKey, *populate(victim, killer).toTypedArray())
    }
  }
}
