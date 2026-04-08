package pw.tmpim.goodfarming.item

import net.minecraft.entity.player.PlayerEntity

@Suppress("PropertyName")
interface DyeItemExt {
  val `goodfarming$usingPlayer`: ThreadLocal<PlayerEntity?>
}
