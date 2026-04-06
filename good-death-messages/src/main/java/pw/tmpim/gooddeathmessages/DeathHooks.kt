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

  val CACTUS = cause(
    "attack.cactus",
    { victim, _ -> victim.data.pricked }
  )

  val DROWN = cause(
    "attack.drown",
    {victim, _ -> victim.air <= 0}
  )

  // Generic Explosion
  val EXPLOSION = cause(
    "attack.explosion",
    {victim, _ -> victim.data.blastSource is ExplosionTracker.EntityBlast}
  )

  // Explosion associated to an entity
  val EXPLOSION_PLAYER = cause(
    "attack.explosion.player",
    {victim, _ -> (victim.data.blastSource as? ExplosionTracker.EntityBlast)?.entity is LivingEntity},
    {victim, _ ->
      listOf(DeathRegistry.getName((victim.data.blastSource as ExplosionTracker.EntityBlast).entity))
    }
  )

  // Explosion associated to a bed
  val EXPLOSION_BED = cause(
    "attack.badRespawnPoint.message",
    {victim, _ -> victim.data.blastSource is ExplosionTracker.BedBlast}
  )

  // A fall of less than 5 blocks
  val FALL = cause(
    "attack.fall",
    {victim, _ -> victim.fallDistance - 3.0f > 0}
  )

  // A fall of more than 5 blocks
  val FALL_FAR = cause(
    "fell.accident.generic",
    {victim, _ -> victim.fallDistance - 5.0f > 0}
  )

  val FALL_LADDER = cause(
    "fell.accident.ladder",
    {victim, _ -> victim.fallDistance - 3.0f > 0 && victim.data.wasClimbing}
  )

  val FALL_WATER = cause(
    "fell.accident.water",
    {victim, _ -> victim.fallDistance - 3.0f > 0 && victim.checkWaterCollisions()}
  )

  val IN_FIRE = cause(
    "attack.inFire",
    {victim, _ -> victim.data.lit}
  )

  val ON_FIRE = cause(
    "attack.onFire",
    {victim, _ -> victim.fireTicks > 0}
  )

  val LAVA = cause(
    "attack.lava",
    {victim, _ -> victim.data.lava}
  )

  val LIGHTNING = cause(
    "attack.lightningBolt",
    {victim, _ -> victim.data.struck}
  )

  val PLAYER = killerCause(
    "attack.player",
    {_, killer -> killer is PlayerEntity}
  )

  val MOB = killerCause(
    "attack.mob",
    {_, killer -> killer is MobEntity}
  )

  val ARROW = cause(
    "attack.arrow",
    {victim, _ -> victim.data.shotBy != null},
    {victim, killer -> listOf(DeathRegistry.getName(killer ?: victim.data.shotBy))}
  )

  val THROWN = cause(
    "attack.thrown",
    {victim, _ -> victim.data.projectile}
  )

  val FIREBALL = cause(
    "attack.fireball",
    {victim, _ ->
      val blast: ExplosionTracker.BlastSource? = victim.data.blastSource
      blast is ExplosionTracker.EntityBlast && blast.entity is FireballEntity
    },
    {victim, _ ->
      val blast: ExplosionTracker.EntityBlast = victim.data.blastSource as ExplosionTracker.EntityBlast
      val owner: Entity = (blast.entity as FireballEntity).owner
      listOf(DeathRegistry.getName(owner))
    }
  )

  val IN_WALL = cause(
    "attack.inWall",
    { victim, _ -> victim.isInsideWall}
  )

  val OUT_OF_WORLD = cause(
    "attack.outOfWorld",
    { victim, _ -> victim.y < -64.0F}
  )

  val GENERIC = cause(
    "attack.generic",
    { _, _ -> true}
  )

  val GENERIC_KILL = cause(
    "attack.genericKill",
    { victim, _ -> victim.data.killCommand}
  )

  // Custom

  val WOLF = killerCause(
    "attack.wolf",
    {_, killer -> killer is WolfEntity}
  )

  val WOLF_PLAYER = cause(
    "attack.wolf.player",
    { _, killer -> killer is WolfEntity && killer.isTamed},
    {_, killer ->
      listOf((killer as WolfEntity).ownerName, DeathRegistry.getName(killer))
    }
  )

  val DROWN_AND_BURN = cause(
    "attack.drownBurn",
    {victim, _ -> victim.air <= 0 && (victim.fireTicks > 0 || victim.data.lit || victim.data.lava)}
  )

  // Cause Class

  fun cause(
    translationKey: String,
    test: (victim: PlayerEntity, killer: Entity?) -> Boolean,
    populate: (victim: PlayerEntity, killer: Entity?) -> List<String> = {
      victim, _ -> listOf(victim.name)
    }
  ) = object : Cause("${namespace}.death." + translationKey) {
    override fun test(victim: PlayerEntity, killer: Entity?): Boolean = test(victim, killer)
    override fun populate(victim: PlayerEntity, killer: Entity?): MutableList<String> =
      super.populate(victim, killer).apply {
        addAll(populate(victim, killer))
      }
  }

  fun killerCause(
    translationKey: String,
    test: (victim: PlayerEntity, killer: Entity?) -> Boolean,
    populate: (victim: PlayerEntity, killer: Entity?) -> List<String> = {
        victim, killer -> listOf(victim.name, DeathRegistry.getName(killer))
    }
  ) = cause(translationKey, test, populate)

  val translations: TranslationStorage = TranslationStorage.getInstance()

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

val Entity.fallDistance: Float
  get() = (this as EntityAccessor).fallDistance
