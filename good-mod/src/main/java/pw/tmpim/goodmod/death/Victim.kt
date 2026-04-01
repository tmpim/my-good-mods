package pw.tmpim.goodmod.death

import pw.tmpim.goodmod.death.ExplosionTracker.BlastSource

@Suppress("FunctionName")
interface Victim {
  // Shot by arrow
  fun goodmod_setShot() {}
  fun goodmod_resetShot() {}
  fun goodmod_isShot(): Boolean = false
  // Pricked by cactus
  fun goodmod_setPricked() {}
  fun goodmod_resetPricked() {}
  fun goodmod_isPricked(): Boolean = false
  // Damaged by an explosion
  fun goodmod_setBlastSource(source: BlastSource?) {}
  fun goodmod_resetBlastSource() {}
  fun goodmod_getBlastSource(): BlastSource? = null
  // Struck by lightning
  fun goodmod_setStruck() {}
  fun goodmod_resetStruck() {}
  fun goodmod_isStruck(): Boolean = false
  // Taking damage from being *in* a fire. Fire ticks are a separate check.
  fun goodmod_setLit() {}
  fun goodmod_resetLit() {}
  fun goodmod_isLit(): Boolean = false
}
