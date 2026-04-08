package pw.tmpim.goodutils.misc

class ClearableLazy<out T>(private val initializer: () -> T) : Lazy<T> {
  private var _value: Any? = UNINITIALIZED

  override val value: T
    get() {
      if (_value === UNINITIALIZED /* equality by reference */) {
        _value = initializer()
      }

      @Suppress("UNCHECKED_CAST")
      return _value as T
    }

  override fun isInitialized() = _value !== UNINITIALIZED

  fun clear() {
    _value = UNINITIALIZED
  }

  override fun toString() = when {
    isInitialized() -> value.toString()
    else -> "Lazy value not initialized yet."
  }

  companion object {
    private object UNINITIALIZED /* uninitialized sentinel */
  }
}

fun <T> clearableLazy(initializer: () -> T) = ClearableLazy(initializer)
