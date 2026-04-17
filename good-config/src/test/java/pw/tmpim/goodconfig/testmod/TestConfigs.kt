package pw.tmpim.goodconfig.testmod

import pw.tmpim.goodconfig.api.ConfigContainer
import pw.tmpim.goodconfig.api.register

object TestConfigs : ConfigContainer {
  val config = TestConfig().register()

  override val configs = setOf(config)
}
