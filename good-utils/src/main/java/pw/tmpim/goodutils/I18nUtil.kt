package pw.tmpim.goodutils

import net.minecraft.item.Item
import net.modificationstation.stationapi.api.registry.ItemRegistry
import net.modificationstation.stationapi.api.util.Identifier
import java.util.*

fun getItemSubKey(item: Item, key: String): String {
  val id = Objects.requireNonNull<Identifier>(ItemRegistry.INSTANCE.getId(item))
  return "item.${id.namespace}.${id.path}.${key}"
}
