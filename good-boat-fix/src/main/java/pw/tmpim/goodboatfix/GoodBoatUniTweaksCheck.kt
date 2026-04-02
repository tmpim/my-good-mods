package pw.tmpim.goodboatfix

import net.danygames2014.unitweaks.UniTweaks

// Kept in a separate class to reduce the impact of linkage errors
object GoodBoatUniTweaksCheck {
  fun checkUniTweaksBoatMixinEnabled(): Boolean
    = UniTweaks.TWEAKS_CONFIG.boatsDropThemselves
}
