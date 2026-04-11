package pw.tmpim.goodflags.client.paint

/**
 * Constants for UI chrome colors used by the flag paint screen.
 */
object PaintColors {
  // Canvas
  const val CANVAS_BORDER = 0xFF333333.toInt()
  const val GRID = 0x20000000

  // Buttons
  const val BTN_BORDER = 0xFF666666.toInt()
  const val BTN_BORDER_SELECTED = 0xFFFFFFFF.toInt()
  const val BTN_BORDER_DISABLED = 0xFF444444.toInt()
  const val BTN_BG = 0xFF444444.toInt()
  const val BTN_BG_SELECTED = 0xFF3A3A6A.toInt()
  const val BTN_BG_DISABLED = 0xFF2A2A2A.toInt()

  // Text
  const val TEXT = 0xFFFFFF
  const val LABEL = 0xAAAAAA
  const val LABEL_DISABLED = 0x555555

  // Palette
  const val PALETTE_BORDER = 0xFF000000.toInt()

  // Overlays
  const val HOVER_ALPHA_MASK = 0xCC000000.toInt()
  const val PREVIEW_ALPHA_MASK = 0x80000000.toInt()

  /** Resolve a border color given selected/disabled state. */
  fun borderColor(selected: Boolean, disabled: Boolean = false): Int = when {
    selected -> BTN_BORDER_SELECTED
    disabled -> BTN_BORDER_DISABLED
    else -> BTN_BORDER
  }

  /** Resolve a background color given selected/disabled state. */
  fun bgColor(selected: Boolean, disabled: Boolean = false): Int = when {
    selected -> BTN_BG_SELECTED
    disabled -> BTN_BG_DISABLED
    else -> BTN_BG
  }
}
