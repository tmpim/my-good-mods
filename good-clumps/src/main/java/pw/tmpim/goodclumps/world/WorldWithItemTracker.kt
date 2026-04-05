package pw.tmpim.goodclumps.world

import net.minecraft.world.World

@Suppress("FunctionName")
interface WorldWithItemTracker {
  fun goodclumps_getItemTracker(): WorldItemTracker
}

val World.itemTracker
  get() = goodclumps_getItemTracker()
