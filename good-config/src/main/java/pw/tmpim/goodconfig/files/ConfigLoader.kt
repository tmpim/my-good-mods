package pw.tmpim.goodconfig.files

import io.github.wasabithumb.jtoml.JToml
import io.github.wasabithumb.jtoml.option.JTomlOption
import io.github.wasabithumb.jtoml.option.JTomlOptions
import io.github.wasabithumb.jtoml.option.prop.IndentationPolicy
import io.github.wasabithumb.jtoml.value.table.TomlTable
import pw.tmpim.goodconfig.GoodConfig.log
import pw.tmpim.goodconfig.api.*
import pw.tmpim.goodconfig.codec.TomlOps
import pw.tmpim.goodutils.misc.loader
import java.nio.file.Path
import kotlin.io.path.bufferedReader
import kotlin.io.path.bufferedWriter
import kotlin.io.path.createParentDirectories
import kotlin.io.path.exists
import kotlin.jvm.optionals.getOrNull

object ConfigLoader {
  private val configs = mutableMapOf<String, MutableSet<ConfigSpec>>()
  private val configFiles = mutableMapOf<Path, ConfigSpec>()

  private val jtoml = JToml.jToml(JTomlOptions.builder()
    .set(JTomlOption.INDENTATION, IndentationPolicy.builder()
      .indentChar(' ')
      .constantIndent(1)
      .build())
    .build())

  /** given a config spec and encoded TomlTable, recursively walk to add descriptions as comments */
  private fun applyCommentsToToml(
    entries: Map<String, SchemaDelegate<*>>,
    tomlTable: TomlTable,
  ) {
    entries.values.forEach { delegate ->
      val key = delegate.serialisedKey
      val desc = delegate.description

      // set description on each entry key
      if (!desc.isNullOrBlank()) {
        val tomlValue = checkNotNull(tomlTable[key]) { "expected $key to exist in $tomlTable" }
        tomlValue.comments().addPre(desc)
      }

      // recurse into nested categories/rows
      val value = delegate.value
      if (value is BaseContainerSpec) {
        val nestedValue = tomlTable[key]
        val nestedTable = checkNotNull(nestedValue as? TomlTable) { "expected $key to be TomlTable, got $nestedValue" }
        applyCommentsToToml(value._entries, nestedTable)
      }
    }
  }

  fun saveConfigToFile(config: ConfigSpec, file: Path) {
    val encoded = config.encodeWith(TomlOps)
    val tomlTable = checkNotNull(encoded.result().getOrNull() as? TomlTable) {
      "failed to serialise config $config into TomlTable: ${encoded.error().getOrNull()}"
    }

    applyCommentsToToml(config._entries, tomlTable)

    file.bufferedWriter().use {
      jtoml.write(it, tomlTable)
    }
  }

  fun loadConfigFromFile(config: ConfigSpec, file: Path) {
    if (!file.exists()) return

    val tomlTable = file.bufferedReader().use {
      jtoml.read(it)
    }.asTable()

    config.decodeWith(TomlOps, tomlTable)
  }

  fun loadRegisteredConfigs() {
    check(configs.isEmpty() && configFiles.isEmpty()) { "loadRegisteredConfigs() called twice!!" }

    // first pass: invoke all the entrypoints and collect the configs
    loader.getEntrypointContainers("good-config", ConfigContainer::class.java).forEach { c ->
      val mod = c.provider
      val modId = mod.metadata.id

      try {
        val modConfigSet = configs.computeIfAbsent(modId) { mutableSetOf() }
        val entrypointConfigs = c.entrypoint.configs.map {
          it.mod = mod
          it
        }
        modConfigSet.addAll(entrypointConfigs)

        log.info("found ${entrypointConfigs.size} configs for $modId (${c.javaClass.name})")
      } catch (e: Exception) {
        throw RuntimeException("failed to load configs for mod $modId", e)
      }
    }

    // second pass: validate the config filenames don't conflict
    configs.asSequence()
      .flatMap { it.value }
      .forEach { config ->
        val modId = config.mod.metadata.id
        val filename = "${config.filename ?: modId}.toml"
        val path = loader.configDir.resolve(modId).resolve(filename)

        check(configFiles.put(path, config) == null) {
          "$modId has multiple configs registered at '$path'. set ConfigSpec.filename to unique filenames"
        }

        config.file = path
      }

    // final pass: load the registered configs from disk and start the file watcher
    configs.asSequence()
      .flatMap { it.value }
      .forEach { config ->
        val file = config.file
        file.createParentDirectories()

        @Suppress("UNCHECKED_CAST")
        val holder = config.holder as? ConfigHolder<ConfigSpec>

        if (file.exists()) {
          // load existing config
          try {
            loadConfigFromFile(config, file)
          } catch (e: Exception) {
            throw RuntimeException("failed to load config file $file", e)
          }
        }

        // save default config, or re-save the current config in case the schema changed
        try {
          holder?.firePreSave(config)
          config.encodeToNbt() // check it can be serialised to NBT
          saveConfigToFile(config, file) // serialise to TOML
          holder?.firePostLoad(config) // though there are probably no listeners right now lol
        } catch (e: Exception) {
          throw RuntimeException("failed to save default config file $file", e)
        }

        ConfigWatcher.startWatching(file, config)
      }
  }
}
