package pw.tmpim.goodconfig.testmod

import pw.tmpim.goodconfig.api.ConfigSpec
import pw.tmpim.goodconfig.api.SyncDirection

class TestConfig : ConfigSpec() {
  val exampleSetting by bool(
    name = "Example setting",
    description = "This is an example of good-config, a configuration library designed specifically for Kotlin"
  )

  val exampleIntSetting by int(
    name = "Example setting",
    description = "This is an example of good-config, a configuration library designed specifically for Kotlin",
    default = 3,
    min = 0,
    max = 5,
    syncDirection = SyncDirection.CLIENT_TO_SERVER
  )

  val exampleFloatSetting by float(
    name = "Example float setting",
    default = 0.5f,
    min = 0.0f,
    max = 2.0f,
    syncDirection = SyncDirection.SERVER_TO_CLIENT
  )

  val exampleBooleanSetting by bool(name = "Example boolean setting")
}
