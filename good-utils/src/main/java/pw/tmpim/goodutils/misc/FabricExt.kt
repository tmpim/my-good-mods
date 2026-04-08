package pw.tmpim.goodutils.misc

import net.fabricmc.api.EnvType
import net.fabricmc.loader.api.FabricLoader

val loader: FabricLoader
  get() = FabricLoader.getInstance()

// shorthands for checking the environment
val envType: EnvType by lazy { loader.environmentType }
val isClient by lazy { envType == EnvType.CLIENT }
val isServer by lazy { envType == EnvType.SERVER }
