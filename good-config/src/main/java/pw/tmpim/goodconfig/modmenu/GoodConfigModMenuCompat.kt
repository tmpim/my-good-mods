package pw.tmpim.goodconfig.modmenu

import net.danygames2014.modmenu.api.ConfigScreenFactory
import net.danygames2014.modmenu.api.ModMenuApi

object GoodConfigModMenuCompat : ModMenuApi {
  override fun getModConfigScreenFactory(): ConfigScreenFactory<*>? = null

  override fun getProvidedConfigScreenFactories(): Map<String, ConfigScreenFactory<*>> {
    return super.getProvidedConfigScreenFactories()
  }
}
