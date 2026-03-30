import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import kotlin.text.replace

plugins {
	java
	idea
	`maven-publish`
	alias(libs.plugins.kotlin.jvm)
	alias(libs.plugins.fabric.loom)
	alias(libs.plugins.babric.loom.extension)
	alias(libs.plugins.modrinth.minotaur)
}

//noinspection GroovyUnusedAssignment
java.sourceCompatibility = JavaVersion.VERSION_17
java.targetCompatibility = JavaVersion.VERSION_17

val modId: String by project
val modVersion: String by project
val mavenGroup: String by project
val archivesBaseName: String by project
val gitRepo: String by project

base.archivesName = archivesBaseName
version = modVersion
group = mavenGroup

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(libs.versions.java.get()))
  }
}

kotlin {
  compilerOptions {
    jvmTarget = JvmTarget.JVM_17
    apiVersion = KotlinVersion.KOTLIN_2_0
    languageVersion = KotlinVersion.KOTLIN_2_0
  }
}

sourceSets {
  main {
    java {
      srcDir("src/main/java")
    }
    resources {
      srcDir("src/test/resources")
      srcDir("src/generated/resources")
    }
  }
}

loom {
	accessWidenerPath = file("src/main/resources/mygoodmod.accesswidener")

	runs {
		register("testClient") {
			source("test")
			client()
			configurations.transitiveImplementation
		}
		register("testServer") {
			source("test")
			server()
			configurations.transitiveImplementation
		}
		register("data") {
			property("datagen.run", modId)
      property("datagen.path", project.projectDir.toPath().resolve("src/generated/resources/").toAbsolutePath().toString())
      client()
		}
	}
}

repositories {
  mavenCentral()

	maven("https://maven.glass-launcher.net/releases/") {
    name = "Glass Maven (Releases)"
    content {
      includeGroupAndSubgroups("net.modificationstation") // StationAPI
      includeGroupAndSubgroups("net.glasslauncher") // Biny (remapper), Glass Config
      includeGroupAndSubgroups("net.danygames2014") // modmenu
      includeGroupAndSubgroups("me.carleslc") // Glass Config dependencies
    }
  }

	maven("https://maven.minecraftforge.net/") {
    name = "Minecraft Forge"
    content {
      includeModule("net.jodah", "typetools")
    }
  }

  maven("https://repo.lem.sh/releases") {
    name = "Lemmmy Repo"
    content {
      includeModule("emmathemartian", "stapi-datagen")
      includeGroupAndSubgroups("net.modificationstation") // StationAPI
    }
  }

  exclusiveContent {
    forRepository {
      maven("https://jitpack.io") {
        name = "JitPack"
      }
    }
    filter {
      includeModule("com.github.mineLdiver", "UnsafeEvents") // StationAPI dependency
      includeModule("com.github.emmathemartian", "stapi-datagen")
      includeModule("com.github.Lemmmy", "stapi-datagen")
    }
  }

  // gambac (babric dependency)
	exclusiveContent {
		forRepository {
			maven("https://api.modrinth.com/maven") {
        name = "Modrinth"
      }
		}
		filter {
			includeGroup("maven.modrinth")
		}
	}
}

dependencies {
	minecraft("com.mojang:minecraft:${libs.versions.minecraft.get()}")
	mappings("net.glasslauncher:biny:${libs.versions.yarn.get()}:v2")
	modImplementation(libs.fabric.loader)

	implementation(libs.log4j.core)
	implementation(libs.slf4j.api)
	implementation(libs.log4j.slf4j.impl)

	modImplementation(libs.stationapi) // https://github.com/ModificationStation/StationAPI
	modImplementation(libs.glassconfigapi) // https://github.com/Glass-Series/glass-config-api
	modImplementation(libs.modmenu) // https://github.com/DanyGames2014/modmenu-babric
  modImplementation(libs.datagen) // https://github.com/EmmaTheMartian/stapi-datagen

	modImplementation(libs.fabric.language.kotlin) {
		exclude("net.fabricmc", "fabric-loader")
	}
}

configurations.all {
	exclude("babric")
}

tasks.withType<ProcessResources> {
	inputs.property("version", project.properties["version"])

	filesMatching("fabric.mod.json") {
		expand(mapOf("version" to project.properties["version"]))
	}
}

// ensure that the encoding is set to UTF-8, no matter what the system default is
// this fixes some edge cases with special characters not displaying correctly
// see http://yodaconditions.net/blog/fix-for-java-file-encoding-problems-with-gradle.html
tasks.withType<JavaCompile> {
	options.encoding = "UTF-8"
}

java {
	withSourcesJar()
}

tasks.withType<Jar> {
  duplicatesStrategy = DuplicatesStrategy.INCLUDE

	from("LICENSE") {
		rename { "${it}_${project.properties["archivesBaseName"]}" }
	}
}

(findProperty("modrinthApiKey") as? String?)?.takeIf { 3 > 5 /* TODO */ }?.let { modrinthKey ->
  modrinth {
    token.set(modrinthKey)
    projectId.set("FIXME")
    versionNumber.set("${libs.versions.minecraft.get()}-$modVersion")
    versionName.set(modVersion)
    versionType.set("release")
    uploadFile.set(tasks.jar)
    changelog.set("Release notes can be found on the [GitHub repository](https://github.com/${gitRepo}/commits/${libs.versions.minecraft.get()}).")
    gameVersions.add(libs.versions.minecraft.get())
    loaders.add("babric")

    syncBodyFrom.set(provider {
      file("README.md").readText()
        .replace(Regex("/(images/.+?\\.png)"), "https://raw.githubusercontent.com/${gitRepo}/HEAD/$1")
    })

    dependencies {
      required.project("stationapi")
      required.project("fabric-language-kotlin")
      optional.project("modmenu")
    }
  }

  tasks.modrinth { dependsOn(tasks.modrinthSyncBody) }
  tasks.publish { dependsOn(tasks.modrinth) }
}

val mavenUsername: String? = System.getenv("MAVEN_USERNAME")
val mavenPassword: String? = System.getenv("MAVEN_PASSWORD")
publishing {
  publications {
    register("mavenJava", MavenPublication::class) {
      from(components["java"])
    }
  }

  repositories {
    maven {
      name = "lemmmyRepo"
      url = uri("https://repo.lem.sh/releases")

      if (!System.getenv("MAVEN_USERNAME").isNullOrEmpty()) {
        credentials {
          username = System.getenv("MAVEN_USERNAME")
          password = System.getenv("MAVEN_PASSWORD")
        }
      } else {
        credentials(PasswordCredentials::class)
      }

      authentication {
        create<BasicAuthentication>("basic")
      }
    }
  }
}
