package pw.tmpim.gooddeathmessages

import net.minecraft.entity.Entity
import net.minecraft.entity.EntityRegistry
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.ArrowEntity
import net.minecraft.entity.projectile.FireballEntity

object DeathRegistry {

  // Registering & Resolution

  private val causes: MutableList<DeathHooks.Cause> = mutableListOf(
    // Explosions
    DeathHooks.FIREBALL, DeathHooks.EXPLOSION_PLAYER, DeathHooks.EXPLOSION_BED, DeathHooks.EXPLOSION,
    // Entity based kills
    DeathHooks.ARROW, DeathHooks.THROWN, DeathHooks.WOLF_PLAYER,
    DeathHooks.WOLF, DeathHooks.PLAYER, DeathHooks.MOB,
    // Accidents
    DeathHooks.CACTUS, DeathHooks.LIGHTNING, DeathHooks.IN_WALL, DeathHooks.FALL_LADDER, DeathHooks.FALL_WATER,
    DeathHooks.FALL_FAR, DeathHooks.FALL, DeathHooks.OUT_OF_WORLD, DeathHooks.DROWN_AND_BURN, DeathHooks.DROWN,
    DeathHooks.ON_FIRE, DeathHooks.IN_FIRE, DeathHooks.LAVA, DeathHooks.GENERIC_KILL
  )

  /** Adds a death cause to the front of the list of death causes.
   */
  @JvmStatic
  fun register(cause: DeathHooks.Cause) {
    causes.addFirst(cause)
  }

  /** Registers a death cause after the given parameter `after`.
   * If `after` does not exist in the list, `cause` is inserted into the front
   */
  @JvmStatic
  fun registerAfter(cause: DeathHooks.Cause, after: DeathHooks.Cause) {
    causes.add(causes.indexOf(after)+1, cause)
  }

  @JvmStatic
  fun createMessage(victim: PlayerEntity, killer: Entity?): String {
    for (cause in causes) {
      if (cause.test(victim, killer)) return cause.translate(victim, killer)
    }
    return DeathHooks.GENERIC.translate(victim, killer)
  }

  fun getName(entity: Entity?): String = when (entity) {
    is PlayerEntity                           -> entity.name
    is FireballEntity if entity.owner != null -> getName(entity.owner)
    is ArrowEntity if entity.owner != null    -> getName(entity.owner)
    else if entity != null                    -> EntityRegistry.getId(entity)
    else                                      -> "Something"
  }
}
