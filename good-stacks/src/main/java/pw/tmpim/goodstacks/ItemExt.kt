package pw.tmpim.goodstacks

import pw.tmpim.goodstacks.GoodStacks.namespace

@Suppress("PropertyName", "FunctionName")
interface ItemExt {
  var `goodstacks$originalMaxCount`: Int?
  fun `goodstacks$setMaxCount`(maxCount: Int)
}

@JvmField
val nbtCountKey = namespace.id("count").toString()
