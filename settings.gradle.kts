pluginManagement {
  repositories {
    maven("https://maven.fabricmc.net/") {
      name = "Fabric"
      content {
        includeGroup("fabric-loom")
        includeGroupAndSubgroups("net.fabricmc")
      }
    }

    maven("https://maven.glass-launcher.net/babric") {
      name = "Glass Maven (Babric)"
      content {
        includeGroupAndSubgroups("babric")
        includeGroup("babric-loom-extension")
      }
    }

    mavenCentral()
    gradlePluginPortal()
  }
}

rootProject.name = "my-good-mods"

// library mods
include("good-utils")
include("good-asset-fetcher")

// standalone mods
include("good-boat-fix")
include("good-clumps")
include("good-command-fixes")
include("good-compression")
include("good-death-messages")
include("good-farming")
include("good-food")
include("good-sign-editing")
include("good-sounds")

// everything else mod/incubator
include("good-mod")
include("good-mod-template")

// non-mods
include("tool-nbt-upgrader")
