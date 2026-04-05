# Good Clumps 

![Minecraft Beta 1.7.3](https://img.shields.io/badge/minecraft-beta_1.7.3-70B237)
[![Modrinth Downloads](https://img.shields.io/modrinth/dt/good-clumps?logo=modrinth)](https://modrinth.com/mod/good-clumps/)
![Maven Version](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Frepo.lem.sh%2Freleases%2Fpw%2Ftmpim%2Fmygoodmods%2Fgood-clumps%2Fmaven-metadata.xml)

<!-- modrinth_exclude.start -->
[![Download on Modrinth](../images/modrinth.png)](https://modrinth.com/mod/good-clumps/)
<!-- modrinth_exclude.end -->

Beta 1.7.3 to merge nearby dropped items together. By default, item entities within a 1×1×1 area will be merged every
2 ticks if they're moving, or every 40 ticks if they're stationary.

## Configuration

The mod's configuration can be configured in-game (if [Mod Menu Babric](<https://modrinth.com/mod/modmenu-babric>) is
installed) or in `.minecraft/config/good-clumps/good-clumps.yml`. The tweak can be disabled without having to
uninstall the mod:

```yml
itemMergeEnabled: true

# The radius, in blocks, of item merging
itemMergeRadius: 0.5

# How frequently, in ticks, dropped items should try to merge with others nearby when still
itemMergeRateStatic: 40

# How frequently, in ticks, dropped items should try to merge with others nearby when moving
itemMergeRateMoving: 2
```

## Requirements

- Minecraft Beta 1.7.3
- [Babric](<https://babric.github.io/use/installer/>)
- [StationAPI](<https://modrinth.com/mod/stationapi>)
- [Fabric Language Kotlin](<https://modrinth.com/mod/fabric-language-kotlin>)

## Recommended

- [Mod Menu Babric](<https://modrinth.com/mod/modmenu-babric>) (for in-game configuration)

## License

This mod is licensed under the [MIT license](../LICENSE). 
