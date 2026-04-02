# my-good-mods


[![Download on Modrinth](/images/modrinth.png)](https://modrinth.com/organization/tmpim)

## Development

To run the monorepo, run the `runClient` root task.

To generate a new subproject, run the `generateMod` task and follow the instructions in the terminal, then add
the subproject to `settings.gradle.kts` and reload the project.

To run data generators, run the subproject's `runData` task.

## Troubleshooting

#### genSources: `Failed to decompile, java.lang.IllegalStateException: Unexpected output: /net/minecraft/class_277$1.java`

Keep running `genSources` or `genSourcesWithVineflower` continuously on a subproject. Do *not* use `genSourcesWithCfr`.

#### `Failed to read classTweaker file from mod` (third-party mod)

```
RuntimeException: Failed to read classTweaker file from mod modmenu
ClassTweakerFormatException: Namespace (intermediary) does not match current runtime namespace (named
```

Run `--refresh-dependencies` with any Gradle task, e.g. `./gradlew clean --refresh-dependencies`.

# License

All mods, code, and assets in this repository are licensed under the [MIT License](./LICENSE) unless otherwise
specified.
