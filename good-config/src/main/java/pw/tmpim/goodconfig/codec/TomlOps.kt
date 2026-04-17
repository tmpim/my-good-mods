package pw.tmpim.goodconfig.codec

import com.mojang.datafixers.util.Pair
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import com.mojang.serialization.MapLike
import io.github.wasabithumb.jtoml.value.TomlValue
import io.github.wasabithumb.jtoml.value.array.TomlArray
import io.github.wasabithumb.jtoml.value.primitive.TomlPrimitive
import io.github.wasabithumb.jtoml.value.primitive.TomlPrimitiveType.*
import io.github.wasabithumb.jtoml.value.table.TomlTable
import java.lang.Long.parseLong
import java.util.function.BiConsumer
import java.util.function.Consumer
import java.util.stream.Stream
import java.util.stream.StreamSupport

object TomlOps : DynamicOps<TomlValue> {
  private val EMPTY_MARKER = TomlTable.create() // no null value, this'll do (for eq. check in mergeToPrimitive)

  override fun empty() = EMPTY_MARKER
  override fun emptyMap() = TomlTable.create()
  override fun emptyList() = TomlArray.create()

  override fun <U> convertTo(
    outOps: DynamicOps<U>,
    input: TomlValue
  ): U = when (input) {
    is TomlTable -> convertMap(outOps, input)
    is TomlArray -> convertList(outOps, input)
    is TomlPrimitive -> when (input.type()) {
      BOOLEAN -> outOps.createBoolean(input.asBoolean())
      STRING  -> outOps.createString(input.asString())
      INTEGER -> when (val v = input.asLong()) {
        v.toByte().toLong()  -> outOps.createByte(v.toByte())
        v.toShort().toLong() -> outOps.createShort(v.toShort())
        v.toInt().toLong()   -> outOps.createInt(v.toInt())
        else -> outOps.createLong(v)
      }
      FLOAT -> when (val v = input.asDouble()) {
        v.toFloat().toDouble() -> outOps.createFloat(v.toFloat())
        else -> outOps.createDouble(v)
      }
      else -> outOps.empty()
    }
    else -> outOps.empty()
  }

  override fun createNumeric(i: Number) = when (i) {
    is Byte   -> TomlPrimitive.of(i.toInt()) // not supported in TOML
    is Double -> TomlPrimitive.of(i)
    is Float  -> TomlPrimitive.of(i)
    is Int    -> TomlPrimitive.of(i)
    is Long   -> TomlPrimitive.of(i)
    is Short  -> TomlPrimitive.of(i.toInt()) // not supported in TOML
    else      -> TomlPrimitive.of(Float.NaN)
  }

  override fun createString(value: String) = TomlPrimitive.of(value)

  override fun createBoolean(value: Boolean) = TomlPrimitive.of(value)

  override fun getNumberValue(input: TomlValue): DataResult<Number> =
    if (input is TomlPrimitive) {
      when (input.type()) {
        // parse as the least lossy type; it will be converted by PrimitiveCodec to the desired type
        FLOAT   -> DataResult.success(input.asDouble())
        INTEGER -> DataResult.success(input.asLong())
        STRING  -> DataResult.success(parseLong(input.asString())) // JsonOps parses as int
        else    -> DataResult.error { "Not a number: $input" }
      }
    } else {
      DataResult.error { "Not a number: $input" }
    }

  override fun getStringValue(input: TomlValue): DataResult<String> =
    when (input) {
      is TomlPrimitive if input.type() == STRING ->
        DataResult.success(input.asString())
      else ->
        DataResult.error { "Not a string: $input" }
    }

  override fun getBooleanValue(input: TomlValue): DataResult<Boolean> =
    when (input) {
      is TomlPrimitive if input.type() == BOOLEAN ->
        DataResult.success(input.asBoolean())
      else ->
        DataResult.error { "Not a boolean: $input" }
    }

  override fun getMap(input: TomlValue): DataResult<MapLike<TomlValue>> =
    when (input)  {
      is TomlTable ->
        DataResult.success(object : MapLike<TomlValue> {
          override fun get(key: TomlValue): TomlValue? = input[key.asString()]
          override fun get(key: String): TomlValue? = input[key]
          override fun entries(): Stream<Pair<TomlValue, TomlValue>> =
            input.keys(false /* top-level only */)
              .stream()
              .map { Pair.of(TomlPrimitive.of(it.toString()), input[it]) }
        })
      else ->
        DataResult.error { "Not a map: $input" }
    }

  override fun createMap(map: Stream<Pair<TomlValue, TomlValue>>) =
    TomlTable.create().apply {
      map.forEach { p ->
        put(p.first.asString(), p.second)
      }
    }

  override fun mergeToMap(
    map: TomlValue,
    key: TomlValue,
    value: TomlValue
  ): DataResult<TomlValue> =
    when {
      map !is TomlTable ->
        DataResult.error({ "mergeToMap called with non-map: $map" }, map)
      key !is TomlPrimitive || key.type() != STRING ->
        DataResult.error({ "key is not a string: $key" }, map)
      else ->
        DataResult.success(TomlTable.copyOf(map).apply {
          put(key.asString(), value)
        })
    }

  override fun mergeToMap(
    map: TomlValue,
    values: MapLike<TomlValue>
  ): DataResult<TomlValue> {
    if (map !is TomlTable) {
      return DataResult.error({ "mergeToMap called with non-map: $map" }, map)
    }

    val valuesIterator = values.entries().iterator()
    if (!valuesIterator.hasNext()) {
      return if (map == empty()) {
        DataResult.success(emptyMap())
      } else {
        DataResult.success(map)
      }
    }

    val out = TomlTable.copyOf(map)

    val missed = mutableListOf<TomlValue>()
    valuesIterator.forEachRemaining { entry ->
      val key = entry.first
      if (key !is TomlPrimitive || key.type() != STRING) {
        missed.add(key)
        return@forEachRemaining
      }

      out.put(key.asString(), entry.second)
    }

    if (!missed.isEmpty()) {
      return DataResult.error({ "some keys are not strings: $missed" }, out)
    }

    return DataResult.success(out)
  }

  override fun getMapValues(input: TomlValue): DataResult<Stream<Pair<TomlValue, TomlValue>>> =
    when (input) {
      is TomlTable ->
        DataResult.success(
          input.keys(false /* top-level only */)
            .stream()
            .map { Pair.of(TomlPrimitive.of(it.toString()), input[it]) }
        )
      else ->
        DataResult.error { "Not a map: $input" }
    }

  override fun getMapEntries(input: TomlValue): DataResult<Consumer<BiConsumer<TomlValue, TomlValue>>> =
    when (input) {
      is TomlTable ->
        DataResult.success(Consumer { c ->
          input.keys(false /* top-level only */)
            .stream()
            .forEach { c.accept(TomlPrimitive.of(it.toString()), input[it]!!) }
        })
      else ->
        DataResult.error { "Not a map: $input" }
    }

  override fun remove(input: TomlValue, key: String): TomlValue =
    when (input)  {
      is TomlTable ->
        // JsonOps returns a new copy of the table, not really sure why but sure. this will probably never get called
        // anyway, since its usage is super internal
        TomlTable.create().apply {
          input.keys(false /* top-level only */)
            .stream()
            .filter { k -> k.toString() != key }
            .forEach { put(it, input[it]!!) }
        }
      else ->
        input
    }

  override fun getList(input: TomlValue): DataResult<Consumer<Consumer<TomlValue>>> =
    when (input) {
      is TomlArray ->
        DataResult.success(Consumer { c ->
          input.forEach { c.accept(it) }
        })
      else ->
        DataResult.error { "Not an array: $input" }
    }

  override fun createList(input: Stream<TomlValue>) =
    TomlArray.create().apply {
      input.forEach { p -> add(p) }
    }

  override fun mergeToList(
    list: TomlValue,
    value: TomlValue
  ): DataResult<TomlValue> =
    when {
      list !is TomlArray ->
        DataResult.error({ "mergeToList called with non-list: $list" }, list)
      else ->
        DataResult.success(TomlArray.copyOf(list).apply {
          add(value)
        })
    }

  override fun mergeToList(
    list: TomlValue,
    values: List<TomlValue>
  ): DataResult<TomlValue> =
    when {
      list !is TomlArray ->
        DataResult.error({ "mergeToList called with non-list: $list" }, list)
      else ->
        DataResult.success(TomlArray.copyOf(list).apply {
          addAll(values)
        })
    }

  override fun getStream(input: TomlValue): DataResult<Stream<TomlValue>> =
    when (input) {
      is TomlArray ->
        DataResult.success(StreamSupport.stream(input.spliterator(), false))
      else ->
        DataResult.error { "Not an array: $input" }
    }

  override fun toString() = "TOML"
}

private fun TomlValue.asString(): String =
  this.asPrimitive().asString()
