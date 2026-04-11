package pw.tmpim.goodflags.client.paint

import pw.tmpim.goodflags.data.TranslationString.TOOL as TT

/**
 * Available tools in the flag paint screen.
 */
enum class PaintTool(val labelKey: String) {
  PENCIL("$TT.pencil"),
  FILL("$TT.fill"),
  ERASER("$TT.eraser"),
  LINE("$TT.line"),
  RECT("$TT.rect"),
  CIRCLE("$TT.circle");

  /** Whether this tool ignores brush size (always acts as size 1). */
  val ignoresBrushSize get() = this == FILL || this == RECT || this == CIRCLE
  val isFreehand get() = this == PENCIL || this == ERASER
  val isShape get() = this == LINE || this == RECT || this == CIRCLE
}
