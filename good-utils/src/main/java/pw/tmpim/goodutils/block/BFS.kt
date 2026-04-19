package pw.tmpim.goodutils.block

import net.minecraft.util.math.BlockPos
import kotlin.math.sqrt

object BFS {
  /**
   * Represents an integral block position as a Kotlin [Triple]. Serves as a data class alternative to
   * [net.minecraft.util.math.BlockPos].
   */
  typealias Pos = Triple<Int, Int, Int>

  fun Pos.distanceTo(bx: Int, by: Int, bz: Int): Double = let { (ax, ay, az) ->
    val dx = (ax - bx).toDouble()
    val dy = (ay - by).toDouble()
    val dz = (az - bz).toDouble()
    sqrt(dx * dx + dy * dy + dz * dz)
  }

  fun Pos.distanceTo(other: Pos): Double = other.let { (bx, by, bz) -> distanceTo(bx, by, bz) }

  fun Pos.toBlockPos(): BlockPos = let { (x, y, z) -> BlockPos(x, y, z) }

  val Pos.x: Int
    get() = let { (x, _, _) -> x }
  val Pos.y: Int
    get() = let { (_, y, _) -> y }
  val Pos.z: Int
    get() = let { (_, _, z) -> z }

  fun getAdjacentBlocks(x: Int, y: Int, z: Int): Sequence<Triple<Int, Int, Int>> = sequence {
    yield(Triple(x + 1, y, z))
    yield(Triple(x - 1, y, z))
    yield(Triple(x, y + 1, z))
    yield(Triple(x, y - 1, z))
    yield(Triple(x, y, z + 1))
    yield(Triple(x, y, z - 1))
  }

  fun getAdjacentBlocks(pos: Pos) = pos.let { (x, y, z) -> getAdjacentBlocks(x, y, z) }

  private typealias DistanceMap = HashMap<Pos, Int>

  data class BFSNode(val pos: Pos, val distance: Int): Comparable<BFSNode> {
    override fun compareTo(other: BFSNode): Int = distance.compareTo(other.distance)
  }

  private fun breadthFirstSearchImpl(
    source: Pos,
    shouldSearch: (Pos) -> Boolean,
    isGoal: (Pos) -> Boolean,
    maxNodes: Int? = null
  ): Sequence<BFSNode> = sequence {
    val queue = ArrayDeque<Pos>()
    queue.addFirst(source)

    val distance: DistanceMap = hashMapOf(source to 0)

    bfsLoop@while (queue.isNotEmpty()) {
      if (maxNodes != null && queue.size >= maxNodes) {
        break@bfsLoop
      }

      val u = queue.removeFirst()

      for (v in getAdjacentBlocks(u).filter(shouldSearch)) {
        if (!distance.containsKey(v)) {
          distance[u]!!.let {
            val distanceToV = it + 1
            distance[v] = distanceToV
            yield(BFSNode(v, distanceToV))

            if (isGoal(v)) {
              break@bfsLoop
            }

            queue.addFirst(v)
          }
        }
      }
    }
  }

  fun breadthFirstSearchFirst(
    source: Pos,
    shouldSearch: (Pos) -> Boolean,
    isGoal: (Pos) -> Boolean,
    maxNodes: Int? = null
  ): Pos? =
    breadthFirstSearchImpl(source, shouldSearch, isGoal, maxNodes)
      .map { it.pos }
      .firstOrNull()

  fun breadthFirstSearch(
    source: Pos,
    shouldSearch: (Pos) -> Boolean,
    maxNodes: Int? = null
  ): Sequence<BFSNode> =
    breadthFirstSearchImpl(source, shouldSearch, { _ -> false }, maxNodes)
}
