# Good Command Fixes

![Minecraft Beta 1.7.3](https://img.shields.io/badge/minecraft-beta_1.7.3-70B237)
[![Modrinth Downloads](https://img.shields.io/modrinth/dt/good-command-fixes?logo=modrinth)](https://modrinth.com/mod/good-command-fixes/)
![Maven Version](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Frepo.lem.sh%2Freleases%2Fpw%2Ftmpim%2Fmygoodmods%2Fgood-command-fixes%2Fmaven-metadata.xml)

<!-- modrinth_exclude.start -->
[![Download on Modrinth](../images/modrinth.png)](https://modrinth.com/mod/good-command-fixes/)
<!-- modrinth_exclude.end -->

Beta 1.7.3 server mod to provide simple command fixes:

- Allow all players to run /list without requiring op
- Show /tell feedback to the sender

![Screenshot of Minecraft Beta 1.7.3 with Good Command Fixes installed on the server. The player runs `/tell` and their 
message is visible in the chat.](images/demo.png)

## Compatibility

This mod is incompatible with [Glass Brigadier](<https://github.com/Glass-Series/Glass-Brigadier>). 

## Requirements

- Minecraft Beta 1.7.3
- [Babric](<https://babric.github.io/use/installer/>)
- [StationAPI](<https://modrinth.com/mod/stationapi>)
- [Fabric Language Kotlin](<https://modrinth.com/mod/fabric-language-kotlin>)
- [Glass Config API](<https://modrinth.com/mod/glass-config-api>)

## Configuration

The mod's configuration can be found at `.minecraft/config/good-command-fixes/good-command-fixes.yml`. Each command
tweak can be individually configured:

```yml
globalListEnabled: true
tellFeedbackEnabled: true
```

## License

This mod is licensed under the [MIT license](../LICENSE). 
