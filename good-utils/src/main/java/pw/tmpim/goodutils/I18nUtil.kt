package pw.tmpim.goodutils

import net.minecraft.client.resource.language.I18n
import net.minecraft.item.Item
import net.modificationstation.stationapi.api.registry.ItemRegistry
import net.modificationstation.stationapi.api.util.Identifier
import java.util.*

fun getItemSubKey(item: Item, key: String): String {
  val id = Objects.requireNonNull<Identifier>(ItemRegistry.INSTANCE.getId(item))
  return "item.${id.namespace}.${id.path}.${key}"
}

fun String.i18n(vararg args: Any): String =
  I18n.getTranslation(this, *args)
