package pw.tmpim.goodstacks

object ItemCount {
  @JvmStatic
  fun formatItemCount(n: Int): String = when {
    n < 1_000         -> "$n"
    n < 10_000        -> "%.1fK".format(n / 1_000.0)
    n < 1_000_000     -> "${n / 1_000}K"
    n < 1_000_000_000 -> "%.1fM".format(n / 1_000_000.0)
    else              -> "%.1fB".format(n / 1_000_000_000.0)
  }
}
