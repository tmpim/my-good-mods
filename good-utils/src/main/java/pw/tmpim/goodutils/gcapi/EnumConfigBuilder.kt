package pw.tmpim.goodutils.gcapi

import com.google.common.collect.ImmutableMap
import net.glasslauncher.mods.gcapi3.api.ConfigEntry
import net.glasslauncher.mods.gcapi3.impl.SeptFunction
import net.glasslauncher.mods.gcapi3.impl.`object`.ConfigEntryHandler
import net.glasslauncher.mods.gcapi3.impl.`object`.entry.EnumConfigEntryHandler
import java.lang.reflect.Field
import java.lang.reflect.Type

typealias GcapiConfigHandlerFn =
  SeptFunction<String, ConfigEntry, Field, Any, Boolean, Any, Any, ConfigEntryHandler<*>>
typealias GcapiBuilder =
  ImmutableMap.Builder<Type, GcapiConfigHandlerFn>

inline fun <reified T : Enum<*>> getEnumBuilder(enum: Class<T>): GcapiConfigHandlerFn =
  SeptFunction { id, configEntry, parentField, parentObject, isMultiplayerSynced, enumOrOrdinal, defaultEnum ->
    val enumOrdinal = enumOrOrdinal as? Int ?: (enumOrOrdinal as T).ordinal

    EnumConfigEntryHandler<T>(
      id,
      configEntry,
      parentField,
      parentObject,
      isMultiplayerSynced,
      enumOrdinal,
      (defaultEnum as T).ordinal,
      enum
    )
  }

inline fun <reified T : Enum<*>> GcapiBuilder.putEnum(enum: Class<T>): GcapiBuilder =
  put(T::class.java, getEnumBuilder(enum))
