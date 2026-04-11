package pw.tmpim.goodflags.client.paint

/**
 * Computed positions for the UI panels surrounding the canvas.
 * Recalculated whenever the screen is resized.
 */
data class PanelLayout(
  val paletteX: Int,
  val paletteY: Int,
  val toolsX: Int,
  val toolsY: Int,
  val brushX: Int,
  val brushY: Int,
) {
  companion object {
    // ── Size/spacing constants ────────────────────────────────────────────
    const val PALETTE_SIZE = 16
    const val MAX_BRUSH_SIZE = 4
    const val PALETTE_CANVAS_GAP = 15
    const val PANEL_GAP = 18

    const val PALETTE_SWATCH_SIZE = 14
    const val PALETTE_GAP = 2
    const val TOOL_BTN_W = 46
    const val TOOL_BTN_H = 16
    const val TOOL_BTN_GAP = 4
    const val BRUSH_BTN_SIZE = 14
    const val BRUSH_BTN_GAP = 4

    const val LABEL_OFFSET_Y = 12
    const val PALETTE_BUTTON_GAP = 10
    const val FONT_HEIGHT = 8
    const val GRID_STEP = 4

    /**
     * Compute the panel layout from the screen dimensions and canvas viewport.
     */
    fun compute(screenWidth: Int, viewport: CanvasViewport): PanelLayout {
      val totalPaletteWidth = PALETTE_SIZE * (PALETTE_SWATCH_SIZE + PALETTE_GAP) - PALETTE_GAP
      val palX = (screenWidth - totalPaletteWidth) / 2
      val palY = viewport.canvasY + viewport.canvasHeight + PALETTE_CANVAS_GAP

      val tlX = viewport.canvasX - TOOL_BTN_W - PANEL_GAP
      val totalToolsH = PaintTool.entries.size * (TOOL_BTN_H + TOOL_BTN_GAP) - TOOL_BTN_GAP
      val tlY = viewport.canvasY + (viewport.canvasHeight - totalToolsH) / 2

      val brX = viewport.canvasX + viewport.canvasWidth + PANEL_GAP
      val brY = tlY

      return PanelLayout(palX, palY, tlX, tlY, brX, brY)
    }
  }
}
