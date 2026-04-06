package pw.tmpim.goodutils.data

import emmathemartian.datagen.util.DataIngredient
import net.modificationstation.stationapi.api.item.json.JsonItemKey
import net.modificationstation.stationapi.api.registry.ItemRegistry
import kotlin.jvm.optionals.getOrNull

// data utilities that *do* require stapi-datagen in classpath
fun DataIngredient.toJsonItemKey(): JsonItemKey {
  val srcItem = item.left()
    .map { checkNotNull(ItemRegistry.INSTANCE.getId(it)) { "item $it not found in registry" } }
    .getOrNull()
  val srcTag = item.right().getOrNull()
  check(srcItem != null || srcTag != null) { "either item or tag must be specified" }

  return JsonItemKey().also { key ->
    key.item = srcItem?.toString()
    key.count = count
    key.damage = damage.takeIf { srcItem != null } ?: 0
    key.setTag(srcTag?.toString())
  }
}
