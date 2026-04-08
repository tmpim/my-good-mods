package pw.tmpim.goodutils.data

import com.mojang.datafixers.util.Either
import net.minecraft.item.Item
import net.modificationstation.stationapi.api.registry.ItemRegistry
import net.modificationstation.stationapi.api.tag.TagKey
import net.modificationstation.stationapi.api.util.Identifier
import pw.tmpim.goodutils.item.ItemOrTag
import pw.tmpim.goodutils.item.ItemWithDamage
import kotlin.jvm.optionals.getOrNull

// based on stapi's JsonItemKey
data class JsonItemKey(
  val id: String?,
  val damage: Int?,
  val tag: String?
) {
  fun getRegisteredItem(): ItemWithDamage {
    val itemId = Identifier.of(checkNotNull(id) { "item is null" })

    return ItemWithDamage(
      item   = checkNotNull(ItemRegistry.INSTANCE.get(itemId)) { "item not found" },
      damage = damage ?: -1
    )
  }

  fun getRegisteredTag(): TagKey<Item> =
    TagKey.of(ItemRegistry.KEY, Identifier.of(checkNotNull(tag) { "tag is null" }))

  fun get(): ItemOrTag = when {
    id != null && tag == null ->
      Either.left(getRegisteredItem())

    id == null && tag != null ->
      Either.right(getRegisteredTag())

    else -> error("Neither id nor tag, or both are specified in the JsonItemKey!")
  }
}

fun ItemOrTag.toJsonItemKey(): JsonItemKey {
  val srcItem = left()
    .map { (id, meta) ->
      checkNotNull(ItemRegistry.INSTANCE.getId(id)) { "item $id not found in registry" } to meta
    }
    .getOrNull()
  val srcTag = right().getOrNull()

  check(srcItem != null || srcTag != null) { "either id or tag must be specified" }

  return JsonItemKey(
    id     = srcItem?.first?.toString(),
    damage = srcItem?.second ?: -1,
    tag    = srcTag?.id?.toString(),
  )
}
