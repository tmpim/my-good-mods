package pw.tmpim.goodstacks.config

import pw.tmpim.goodutils.i18n.i18n
import pw.tmpim.goodutils.misc.isClient

private const val C = CONFIG_KEY

enum class ItemCountTooltip(val langKey: String) {
  NEVER("$C.item_count_tooltips.never"),
  IF_ABBREVIATED("$C.item_count_tooltips.if_abbreviated"),
  ALWAYS("$C.item_count_tooltips.always");

  override fun toString() = if (isClient) langKey.i18n() else langKey
}
