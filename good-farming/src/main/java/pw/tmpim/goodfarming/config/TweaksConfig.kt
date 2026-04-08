package pw.tmpim.goodfarming.config

import net.minecraft.entity.player.PlayerEntity
import pw.tmpim.goodfarming.GoodFarming.config
import pw.tmpim.goodfarming.config.PlayerConfigurationRegistry.getPlayerConfiguration

object TweaksConfig {
  fun isTramplingNerfEnabled(player: PlayerEntity) =
    config.tramplingNerfEnabled == true
      && getPlayerConfiguration(player).tramplingNerfEnabled

  fun isBonemealWastageFixEnabled(player: PlayerEntity) =
    config.bonemealWastageFixEnabled == true
      && getPlayerConfiguration(player).bonemealWastageFixEnabled

  fun isQuickReplantingEnabled(player: PlayerEntity) =
    config.quickReplantingEnabled == true
      && getPlayerConfiguration(player).quickReplantingEnabled

  fun isSeedBagAutoPickupEnabled(player: PlayerEntity) =
    config.seedBagAutoPickupEnabled == true
      && getPlayerConfiguration(player).seedBagAutoPickupEnabled
}
