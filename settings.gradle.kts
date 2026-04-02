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

// mods
include("good-asset-fetcher")
include("good-death-messages")
include("good-sign-editing")

include("good-mod")

// non-mods
include("tool-nbt-upgrader")
