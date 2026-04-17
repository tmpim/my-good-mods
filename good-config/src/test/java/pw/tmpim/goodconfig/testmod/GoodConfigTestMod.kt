package pw.tmpim.goodconfig.testmod

import net.fabricmc.api.ModInitializer

object GoodConfigTestMod : ModInitializer {
  override fun onInitialize() {
    println("example setting: ${TestConfigs.config.exampleSetting}")
  }
}
