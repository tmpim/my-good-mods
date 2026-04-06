package pw.tmpim.goodflags.block

object FlagSpec {
  const val FLAG_WIDTH = 48
  const val FLAG_HEIGHT = 32

  // Can't use DyeItem.colors because light gray is wrong in there lmao
  val colors = longArrayOf(0x1E1B1B, 0xB3312C, 0x3B511A, 0x51301A, 0x253192, 0x7B2FBE, 0x287697, 0xABABAB, 0x434343, 0xD88198, 0x41CD34, 0xDECF2A, 0x6689D3, 0xC354CD, 0xEB8844, 0xF0F0F0)
  fun getGLColor(dyeIndex: Int): Int = (0xFF000000 or colors[dyeIndex]).toInt()
}
