package pw.tmpim.goodutils.item

import com.mojang.datafixers.util.Either
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.modificationstation.stationapi.api.registry.ItemRegistry
import net.modificationstation.stationapi.api.tag.TagKey
import kotlin.jvm.optionals.getOrNull

// TODO: support NBT querying?
data class ItemWithDamage(val item: Item, val damage: Int = -1) {
  constructor(stack: ItemStack) : this(stack.item, stack.damage)
}

typealias ItemOrTag = Either<ItemWithDamage, TagKey<Item>>

fun ItemOrTag.matches(other: ItemOrTag): Boolean =
  map(
    { (item, damage) ->
      other.map(
        { (item2, damage2) -> item == item2 && (damage == -1 || damage2 == -1 || damage == damage2) },
        { tag2 -> item.registryEntry.isIn(tag2) }
      )
    },
    { tag -> other == tag /* TODO */ }
  )

fun ItemOrTag.matches(other: ItemStack): Boolean =
  map(
    { (item, damage) -> item == other.item && (damage == -1 || damage == other.damage) },
    { tag -> other.isIn(tag) }
  )

fun ItemOrTag.toFirstItem(): Item? =
  map(
    { (item) -> item },
    { tag -> ItemRegistry.INSTANCE.getEntryList(tag).getOrNull()?.firstOrNull()?.value() },
  )

fun ItemOrTag.toFirstItemStack(count: Int = 1): ItemStack? =
  map(
    { (item, damage) -> ItemStack(item, count, damage.takeUnless { it == -1 } ?: 0) },
    { tag -> ItemRegistry.INSTANCE.getEntryList(tag).getOrNull()?.firstOrNull()?.value()?.let { ItemStack(it) } },
  )
