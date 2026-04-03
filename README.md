# my-good-mods

[![Download on Modrinth](/images/modrinth.png)](https://modrinth.com/organization/tmpim)

## Development

Java 21 is required.

To run the monorepo, run the `runClient` root task.

To generate a new subproject, run the `generateMod` task and follow the instructions in the terminal, then add
the subproject to `settings.gradle.kts` and reload the project.

To run data generators, run the subproject's `runData` task.

## Troubleshooting

#### `Critical injection failure: Variable modifier method stationapi_changeTickY(I)I`

An older version of StationAPI has entered your classpath. Run `clean --refresh-dependencies`. Don't run `genSources` on 
the root project—only run it on individual subprojects.

#### genSources: `Failed to decompile, java.lang.IllegalStateException: Unexpected output: /net/minecraft/class_277$1.java`

Keep running `genSources` or `genSourcesWithVineflower` continuously on a subproject. Do *not* use `genSourcesWithCfr`.

#### `Failed to read classTweaker file from mod` (third-party mod)

```
RuntimeException: Failed to read classTweaker file from mod modmenu
ClassTweakerFormatException: Namespace (intermediary) does not match current runtime namespace (named
```

Ensure all inter-subproject dependencies are included with the `namedElements` configuration:

```kts
// good-compression/build.gradle.kts
dependencies {
  implementation(project(path = ":good-asset-fetcher", configuration = "namedElements"))
}
```

Then run `--refresh-dependencies` with any Gradle task, e.g. `./gradlew clean --refresh-dependencies`.

# License

All mods, code, and assets in this repository are licensed under the [MIT License](./LICENSE) unless otherwise
specified.
