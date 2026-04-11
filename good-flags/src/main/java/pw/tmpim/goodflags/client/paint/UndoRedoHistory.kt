package pw.tmpim.goodflags.client.paint

/**
 * Fixed-capacity undo/redo history operating on byte-array snapshots.
 */
class UndoRedoHistory(private val maxSize: Int = 32) {
  private val undoStack = ArrayDeque<ByteArray>(maxSize)
  private val redoStack = ArrayDeque<ByteArray>(maxSize)

  val canUndo get() = undoStack.isNotEmpty()
  val canRedo get() = redoStack.isNotEmpty()

  /** Push a snapshot onto the undo stack and clear the redo stack. */
  fun pushUndo(snapshot: ByteArray) {
    if (undoStack.size >= maxSize) undoStack.removeFirst()
    undoStack.addLast(snapshot)
    redoStack.clear()
  }

  /**
   * Undo: saves [currentState] to redo and returns the previous state,
   * or `null` if nothing to undo.
   */
  fun undo(currentState: ByteArray): ByteArray? {
    if (!canUndo) return null
    redoStack.addLast(currentState.copyOf())
    return undoStack.removeLast()
  }

  /**
   * Redo: saves [currentState] to undo and returns the next state,
   * or `null` if nothing to redo.
   */
  fun redo(currentState: ByteArray): ByteArray? {
    if (!canRedo) return null
    undoStack.addLast(currentState.copyOf())
    return redoStack.removeLast()
  }
}
