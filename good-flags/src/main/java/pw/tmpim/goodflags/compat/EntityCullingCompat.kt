package pw.tmpim.goodflags.compat

import dev.tr7zw.entityculling.EntityCullingMod
import pw.tmpim.goodflags.block.FlagBlockEntity

object EntityCullingCompat {
    fun register() {
        EntityCullingMod.instance.addDynamicBlockEntityWhitelist { it is FlagBlockEntity }
    }
}
