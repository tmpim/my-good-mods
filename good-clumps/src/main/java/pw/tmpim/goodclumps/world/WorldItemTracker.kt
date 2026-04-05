package pw.tmpim.goodclumps.world

import it.unimi.dsi.fastutil.Hash
import it.unimi.dsi.fastutil.HashCommon
import it.unimi.dsi.fastutil.longs.Long2ReferenceOpenHashMap
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenCustomHashMap
import it.unimi.dsi.fastutil.objects.ReferenceArrayList
import net.minecraft.entity.Entity
import net.minecraft.entity.ItemEntity
import net.minecraft.item.ItemStack
import net.modificationstation.stationapi.impl.util.math.ChunkSectionPos
import net.modificationstation.stationapi.impl.util.math.ChunkSectionPos.getSectionCoordFloored
import pw.tmpim.goodclumps.GoodClumps.config
import pw.tmpim.goodclumps.entity.isMergeable
import pw.tmpim.goodclumps.entity.trackedSectionKey
import pw.tmpim.goodclumps.entity.trackedStack
import pw.tmpim.goodclumps.item.hashItemAndNbt
import kotlin.math.max
import kotlin.math.min

/**
 * responsible for keeping track of all item entities in the world. maintains a set of sections (16^3 areas) and their
 * item entities grouped by category (currently just item id + damage)
 */
class WorldItemTracker {
  /** keyed by packed by section key (ChunkSectionPos.asLong) */
  private val sections = Long2ReferenceOpenHashMap<SectionItemIndex>()

  private fun getOrCreateSection(key: Long) =
    sections.getOrPut(key) { SectionItemIndex(Object2ReferenceOpenCustomHashMap(itemHashStrategy)) }

  private fun removeSection(key: Long) {
    sections.remove(key)
  }

  private fun addEntityToSection(key: Long, item: ItemEntity, stack: ItemStack) {
    val section = getOrCreateSection(key)
    section.itemsByCategory.getOrPut(stack, ::ReferenceArrayList).add(item)
  }

  private fun removeEntityFromSection(key: Long, item: ItemEntity, stack: ItemStack) {
    val section = sections.get(key) ?: return
    val list = section.itemsByCategory[stack] ?: return

    list.remove(item)

    // clean up the lists if they're empty now
    if (list.isEmpty) {
      section.itemsByCategory.remove(stack)
      if (section.itemsByCategory.isEmpty()) removeSection(key)
    }
  }

  fun addTrackedItem(item: ItemEntity) {
    val key = sectionKeyForEntity(item)
    addEntityToSection(key, item, item.stack)
    item.trackedSectionKey = key
    item.trackedStack = item.stack.copy()
  }

  fun removeTrackedItem(item: ItemEntity) {
    val key = item.trackedSectionKey.takeUnless { it == Long.MIN_VALUE } ?: return
    val trackedStack = checkNotNull(item.trackedStack) {
      "removeTrackedItem: item at ${item.x} ${item.y} ${item.z} ($item) has a null tracked stack!"
    }
    removeEntityFromSection(key, item, trackedStack)
    item.trackedSectionKey = Long.MIN_VALUE
  }

  fun updateTrackedItem(item: ItemEntity) {
    val trackedSectionKey = item.trackedSectionKey
    val trackedStack = item.trackedStack

    val currentKey = sectionKeyForEntity(item)
    val currentStack = checkNotNull(item.stack) { "item at ${item.x} ${item.y} ${item.z} ($item) has a null stack!" }

    if (
      currentKey != trackedSectionKey // section changed
      || !itemsEqual(currentStack, trackedStack) // stack (excluding count) changed
    ) {
      // remove it from the old section if it's already tracked
      if (trackedSectionKey != Long.MIN_VALUE) {
        removeEntityFromSection(
          trackedSectionKey,
          item,
          checkNotNull(trackedStack) { "tracked item at ${item.x} ${item.y} ${item.z} ($item) had a null stack!" }
        )
      }

      // add it to the new section
      if (currentStack.count > 0) {
        addEntityToSection(currentKey, item, currentStack)
      }

      item.trackedSectionKey = currentKey
      item.trackedStack = currentStack.copy()
    }
  }

  fun mergeWithNeighbours(a: ItemEntity) {
    if (!a.isMergeable() || a.trackedSectionKey == Long.MIN_VALUE) {
      return
    }

    // collect candidate sections (adjacent cubic chunks). the item's grown AABB may overlap up to 8 sections
    val r = config.itemMergeRadius ?: 0.5
    val minSX = getSectionCoordFloored(a.x - r)
    val minSY = getSectionCoordFloored(a.y - r)
    val minSZ = getSectionCoordFloored(a.z - r)
    val maxSX = getSectionCoordFloored(a.x + r)
    val maxSY = getSectionCoordFloored(a.y + r)
    val maxSZ = getSectionCoordFloored(a.z + r)

    val aStack = a.stack
    var remaining = aStack.maxCount - aStack.count

    for (sx in minSX..maxSX) {
      for (sy in minSY..maxSY) {
        for (sz in minSZ..maxSZ) {
          val section = sections[ChunkSectionPos.asLong(sx, sy, sz)] ?: continue
          val candidates = section.itemsByCategory[aStack] ?: continue

          candidates.forEach { b ->
            // skip ourselves, unmergeables, and anything too far away
            // TODO: do we need to do a real item equality check here? the map should be sufficient
            if (b == a || !b.isMergeable() || !overlaps(a, b, r)) {
              return@forEach
            }

            // check we have anything we can carry over
            val bStack = b.stack
            val transferable = minOf(bStack.count, remaining)
            if (transferable <= 0) {
              return@forEach // one of us is all done merging, but hopefully this should never get hit
            }

            // perform the merge
            bStack.count -= transferable
            aStack.count += transferable
            remaining -= transferable

            a.pickupDelay = max(a.pickupDelay, b.pickupDelay)
            a.age = min(a.age, b.age)

            if (bStack.count <= 0) {
              b.markDead() // entity will get removed later in the tick/next tick
            }

            if (remaining <= 0) {
              return // a is all done merging
            }
          }
        }
      }
    }
  }

  /** represents a 16^3 cubic section of the world */
  private class SectionItemIndex(
    /** map of item stack categories (currently just item id + damage) to item entity IDs */
    val itemsByCategory: Object2ReferenceOpenCustomHashMap<ItemStack, ReferenceArrayList<ItemEntity>>
  )

  companion object {
    /** used by the fastutil hashing strategy and the item mixin, exposed to keep impls consistent */
    fun hashItem(stack: ItemStack?) = HashCommon.mix(stack.hashItemAndNbt())
    fun itemsEqual(a: ItemStack?, b: ItemStack?) = a == b || a != null && b != null && a.isItemEqual(b)
    // (including count) // fun stacksEqual(a: ItemStack?, b: ItemStack?) = ItemStack.areEqual(a, b)

    private val itemHashStrategy = object : Hash.Strategy<ItemStack> {
      override fun hashCode(stack: ItemStack) = hashItem(stack)
      override fun equals(a: ItemStack?, b: ItemStack?) = itemsEqual(a, b)
    }

    /** ChunkSectionPos.from(Position) */
    private fun sectionKeyForEntity(entity: Entity) =
      ChunkSectionPos.asLong(
        getSectionCoordFloored(entity.x),
        getSectionCoordFloored(entity.y),
        getSectionCoordFloored(entity.z),
      )

    private fun overlaps(a: ItemEntity, b: ItemEntity, r: Double): Boolean {
      val dx = a.x - b.x
      val dy = a.y - b.y
      val dz = a.z - b.z
      return dx > -r && dx < r && dy > -r && dy < r && dz > -r && dz < r
    }
  }
}
