package pw.tmpim.gooddeathmessages

import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.MobEntity
import net.minecraft.entity.passive.WolfEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.FireballEntity
import pw.tmpim.gooddeathmessages.DeathHooks.Cause
import pw.tmpim.gooddeathmessages.ExplosionTracker.BedBlast
import pw.tmpim.gooddeathmessages.ExplosionTracker.EntityBlast
import pw.tmpim.gooddeathmessages.GoodDeathMessages.MOD_ID
import pw.tmpim.gooddeathmessages.mixin.EntityAccessor
import pw.tmpim.goodutils.i18n.i18n

object DeathHooks {
  // ┌──────────────────────────────────────────────────────────┐
  // │                   Vanilla death causes                   │
  // └──────────────────────────────────────────────────────────┘
  val CACTUS = cause("attack.cactus", { v, _ -> v.data.pricked })
  val DROWN  = cause("attack.drown", { v, _ -> v.air <= 0 })

  val EXPLOSION = cause("attack.explosion", { v, _ -> v.data.blastSource is EntityBlast })
  val EXPLOSION_PLAYER = cause(
    "attack.explosion.player",
    { v, _ -> (v.data.blastSource as? EntityBlast)?.entity is LivingEntity },
    { v, _ -> listOf(DeathRegistry.getName((v.data.blastSource as EntityBlast).entity)) }
  )
  val EXPLOSION_BED = cause("attack.badRespawnPoint.message", { v, _ -> v.data.blastSource is BedBlast })

  // A fall of less than 5 blocks
  val FALL        = cause("attack.fall", { v, _ -> v.fallDistance - 3.0f > 0 })
  // A fall of more than 5 blocks
  val FALL_FAR    = cause("fell.accident.generic", { v, _ -> v.fallDistance - 5.0f > 0 })
  val FALL_LADDER = cause("fell.accident.ladder", { v, _ -> v.fallDistance - 3.0f > 0 && v.data.wasClimbing })

  val IN_FIRE   = cause("attack.inFire", { v, _ -> v.data.lit })
  val ON_FIRE   = cause("attack.onFire", { v, _ -> v.fireTicks > 0 })
  val LAVA      = cause("attack.lava", { v, _ -> v.data.lava })
  val LIGHTNING = cause("attack.lightningBolt", { v, _ -> v.data.struck })

  val PLAYER = killerCause("attack.player", { _, k -> k is PlayerEntity })
  val MOB    = killerCause("attack.mob", { _, k -> k is MobEntity })

  val ARROW = cause("attack.arrow",
    { v, _ -> v.data.shotBy != null },
    { v, k -> listOf(DeathRegistry.getName(k ?: v.data.shotBy)) }
  )
  val THROWN = cause("attack.thrown", { v, _ -> v.data.projectile })
  val FIREBALL = cause(
    "attack.fireball",
    { v, _ -> (v.data.blastSource as? EntityBlast)?.entity is FireballEntity },
    { v, _ ->
      val blast = v.data.blastSource as EntityBlast
      val owner = (blast.entity as FireballEntity).owner
      listOf(DeathRegistry.getName(owner))
    }
  )

  val IN_WALL      = cause("attack.inWall", { v, _ -> v.isInsideWall })
  val OUT_OF_WORLD = cause("attack.outOfWorld", { v, _ -> v.y < -64.0f })
  val GENERIC_KILL = cause("attack.genericKill", { v, _ -> v.data.killCommand })

  // fallback if nothing else passes the test
  val GENERIC = cause("attack.generic", { _, _ -> true })

  // ┌──────────────────────────────────────────────────────────┐
  // │  Custom, non-vanilla hooks added by Good Death Messages  │
  // └──────────────────────────────────────────────────────────┘
  val WOLF = killerCause(
    "attack.wolf",
    { _, k -> k is WolfEntity },
    custom = true
  )
  val WOLF_PLAYER = cause(
    "attack.wolf.player",
    { _, k -> k is WolfEntity && k.isTamed },
    { _, k -> listOf((k as WolfEntity).ownerName, DeathRegistry.getName(k)) },
    custom = true
  )

  val FALL_WATER  = cause(
    "fell.accident.water",
    { v, _ -> v.fallDistance - 3.0f > 0 && v.checkWaterCollisions() },
    custom = true
  )

  val DROWN_AND_BURN = cause(
    "attack.drownBurn",
    { v, _ -> v.air <= 0 && (v.fireTicks > 0 || v.data.lit || v.data.lava) },
    custom = true
  )

  abstract class Cause(val translationKey: String) {
    abstract fun test(victim: PlayerEntity, killer: Entity?): Boolean

    open fun populate(victim: PlayerEntity, killer: Entity?): MutableList<String> =
      mutableListOf(victim.name)

    internal fun translate(victim: PlayerEntity, killer: Entity?): String =
      translationKey.i18n(*populate(victim, killer).toTypedArray())

    internal fun values(victim: PlayerEntity, killer: Entity?): Pair<String, Array<String>> =
      Pair(translationKey, populate(victim, killer).toTypedArray())
  }
}

private val Entity.fallDistance: Float
  get() = (this as EntityAccessor).fallDistance

// shorthands for registering causes
private typealias TestFn = (victim: PlayerEntity, killer: Entity?) -> Boolean
private typealias PopulateFn = (victim: PlayerEntity, killer: Entity?) -> List<String>

private fun cause(
  translationKey: String,
  testFn: TestFn,
  populateFn: PopulateFn = { _, _ -> listOf() },
  custom: Boolean = false
) = object : Cause("${MOD_ID}.death.$translationKey") {
  override fun test(victim: PlayerEntity, killer: Entity?) =
    testFn(victim, killer) && (!custom || GoodDeathMessages.config.customDeathMessagesEnabled == true)

  override fun populate(victim: PlayerEntity, killer: Entity?) =
    super.populate(victim, killer)
      .apply { addAll(populateFn(victim, killer)) }
}

private fun killerCause(
  translationKey: String,
  testFn: TestFn,
  populateFn: PopulateFn = { _, k -> listOf(DeathRegistry.getName(k)) },
  custom: Boolean = false
) = cause(translationKey, testFn, populateFn, custom)
