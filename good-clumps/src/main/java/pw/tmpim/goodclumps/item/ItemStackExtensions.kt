package pw.tmpim.goodclumps.item

import net.minecraft.item.ItemStack
import java.util.Objects

fun ItemStack?.hashItemAndNbt(): Int =
  when {
    this == null -> 0
    // TODO: doesn't account for stationNbt at the moment, as NBT doesn't have a hashing implementation. this won't
    //       cause any problems, but it could mean the search space isn't restricted as much as it could be
    else -> Objects.hash(item, damage)
  }
