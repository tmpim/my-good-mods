package pw.tmpim.mygoodmod.death

import pw.tmpim.mygoodmod.death.ExplosionTracker.BlastSource

@Suppress("FunctionName")
interface Victim {
  // Shot by arrow
  fun goodmod_setShot()
  fun goodmod_resetShot()
  fun goodmod_isShot(): Boolean
  // Pricked by cactus
  fun goodmod_setPricked()
  fun goodmod_resetPricked()
  fun goodmod_isPricked(): Boolean
  // Damaged by an explosion
  fun goodmod_setBlasted(source: BlastSource?)
  fun goodmod_resetBlasted()
  fun goodmod_isBlasted(): BlastSource?
  // Struck by lightning
  fun goodmod_setStruck()
  fun goodmod_resetStruck()
  fun goodmod_isStruck(): Boolean
  // Taking damage from being *in* a fire. Fire ticks are a separate check.
  fun goodmod_setLit()
  fun goodmod_resetLit()
  fun goodmod_isLit(): Boolean
}
