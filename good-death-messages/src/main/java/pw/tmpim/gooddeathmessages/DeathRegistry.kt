package pw.tmpim.gooddeathmessages

import net.minecraft.entity.Entity
import net.minecraft.entity.EntityRegistry
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.ArrowEntity
import net.minecraft.entity.projectile.FireballEntity

object DeathRegistry {
  // Registering & Resolution
  private val causes = with (DeathHooks) {
    // vanilla causes + non-vanilla causes, which dynamically test customDeathMessagesEnabled (which may change at
    // runtime if modmenu is installed)
    mutableListOf(
      // Explosions
      FIREBALL, EXPLOSION_PLAYER, EXPLOSION_BED, EXPLOSION,

      // Entity based kills
      ARROW, THROWN, WOLF_PLAYER, WOLF, PLAYER, MOB,

      // Accidents
      CACTUS, LIGHTNING, IN_WALL, FALL_LADDER, FALL_WATER, FALL_FAR, FALL, OUT_OF_WORLD, DROWN_AND_BURN, DROWN, ON_FIRE,
      IN_FIRE, LAVA, GENERIC_KILL
    )
  }

  /**
   * Adds a death cause to the front of the list of death causes.
   */
  @JvmStatic
  fun register(cause: DeathHooks.Cause) {
    causes.addFirst(cause)
  }

  /**
   * Registers a death cause after the given parameter `after`.
   * If `after` does not exist in the list, `cause` is inserted into the front.
   */
  @JvmStatic
  fun registerAfter(cause: DeathHooks.Cause, after: DeathHooks.Cause) {
    causes.add(causes.indexOf(after) + 1, cause)
  }

  @JvmStatic
  fun createMessage(victim: PlayerEntity, killer: Entity?): String? {
    if (GoodDeathMessages.config.deathMessagesEnabled != true) {
      return null
    }

    for (cause in causes) {
      if (cause.test(victim, killer)) {
        return cause.translate(victim, killer)
      }
    }

    return DeathHooks.GENERIC.translate(victim, killer)
  }

  fun getName(e: Entity?): String = when (e) {
    is PlayerEntity                      -> e.name
    is FireballEntity if e.owner != null -> getName(e.owner)
    is ArrowEntity if e.owner != null    -> getName(e.owner)
    else if e != null                    -> EntityRegistry.getId(e)
    else                                 -> "Something"
  }
}
