package pw.tmpim.goodclumps.entity

import net.minecraft.entity.ItemEntity
import net.minecraft.item.ItemStack
import pw.tmpim.goodclumps.GoodClumps.config
import kotlin.math.floor

@Suppress("FunctionName")
interface TrackedItemEntity {
  fun goodclumps_getTrackedSectionKey(): Long = Long.MIN_VALUE // sentinel for "not yet tracked"
  fun goodclumps_setTrackedSectionKey(key: Long) {}

  fun goodclumps_getTrackedStack(): ItemStack? = null
  fun goodclumps_setTrackedStack(stack: ItemStack) {}

  companion object {
    @JvmStatic
    fun isTimeToMerge(item: ItemEntity): Boolean {
      val moved = floor(item.prevX) != floor(item.x)
        || floor(item.prevY) != floor(item.y)
        || floor(item.prevZ) != floor(item.z)

      val rate = if (moved) {
        config.itemMergeRateMoving ?: 2
      } else {
        config.itemMergeRateStatic ?: 40
      }

      return item.itemTicks % rate == 0
    }
  }
}

var ItemEntity.trackedSectionKey
  get() = goodclumps_getTrackedSectionKey()
  set(value) { goodclumps_setTrackedSectionKey(value) }

var ItemEntity.trackedStack
  get() = goodclumps_getTrackedStack()
  set(value) { goodclumps_setTrackedStack(checkNotNull(value) { "can't set tracked stack to null" }) }

fun ItemEntity.isMergeable() =
  !dead
    && age < 6000
    && stack.count < stack.maxCount
    /* modern vanilla feature; pickupDelay of 32767 means an item cannot be picked up. not used in b1.7.3, but maybe
     * another mod implements it! */
    && pickupDelay != 32767
    /* another modern vanilla feature; age of -32768 means an item cannot despawn. not used in b1.7.3. */
    && age != -32768
