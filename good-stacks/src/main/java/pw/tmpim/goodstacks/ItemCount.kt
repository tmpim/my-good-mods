package pw.tmpim.goodstacks

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.item.ItemStack
import pw.tmpim.goodstacks.GoodStacks.config
import pw.tmpim.goodstacks.config.ItemCountTooltip.ALWAYS
import pw.tmpim.goodstacks.config.ItemCountTooltip.IF_ABBREVIATED
import pw.tmpim.goodutils.i18n.i18n

object ItemCount {
  @JvmStatic
  fun formatItemCount(n: Int): String = when {
    n < 1_000         -> "$n"
    n < 10_000        -> "%.1fK".format(n / 1_000.0)
    n < 1_000_000     -> "${n / 1_000}K"
    n < 1_000_000_000 -> "%.1fM".format(n / 1_000_000.0)
    else              -> "%.1fB".format(n / 1_000_000_000.0)
  }

  @Environment(EnvType.CLIENT)
  fun renderItemCountTooltip(stack: ItemStack): String? {
    val tooltips = config.itemCountTooltips

    return if (
      tooltips == ALWAYS
      || (tooltips == IF_ABBREVIATED && stack.count >= 1000 && config.shortenItemCounts == true)
    ) {
      val count = "%,d".format(stack.count)
      val name = stack.translationKey?.let { "$it.name".i18n() }
      "§7${count}x ${name ?: ""}"
    } else {
      null
    }
  }
}
