@file:Suppress("UnstableApiUsage")

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
val mavenGroup: String by project
val archivesBaseName: String by project
val gitRepo: String by project

base.archivesName = archivesBaseName
version = "1.0.0" // dummy root version, unused
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

allprojects {
  val libs = rootProject.libs // https://github.com/gradle/gradle/issues/16634#issuecomment-809345790

  project.properties["modVersion"]?.let { version = it }

  apply(plugin = "kotlin")
  apply(plugin = "maven-publish")

  // shared repositories
  repositories {
    mavenCentral()

    maven("https://maven.glass-launcher.net/releases/") {
      name = "Glass Maven (Releases)"
      content {
        includeGroupAndSubgroups("net.modificationstation") // StationAPI
        includeGroupAndSubgroups("net.glasslauncher") // Biny (remapper), Glass Config, Always More Items
        includeGroupAndSubgroups("net.danygames2014") // modmenu, UniTweaks
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
        includeGroupAndSubgroups("sh.lem")
        includeGroupAndSubgroups("pw.tmpim")
        includeModule("net.querz.nbt", "NBT")
        includeModule("emmathemartian", "stapi-datagen") // (temporary, hopefully)
        includeGroupAndSubgroups("net.modificationstation") // StationAPI (temporary, hopefully)
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
        includeModule("com.github.paulevsGitch", "BHCreative")
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

  // minecraft-specific things
  if (!name.startsWith("tool-")) {
    apply(plugin = "fabric-loom")
    apply(plugin = "babric-loom-extension")

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

    // shared dependencies
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

      // optional support
      modRuntimeOnly(libs.bhcreative) { // https://github.com/paulevsGitch/BHCreative
        exclude("net.modificationstation", "StationAPI")
      }
      modRuntimeOnly(libs.alwaysmoreitems) { // https://github.com/Glass-Series/Always-More-Items
        exclude("net.modificationstation", "StationAPI")
      }

      modImplementation(libs.fabric.language.kotlin) {
        exclude("net.fabricmc", "fabric-loader")
      }
    }

    loom {
      // class tweaker
      val classTweakerPath = file("src/main/resources/$name.classtweaker")
      if (classTweakerPath.exists()) {
        println("found class tweaker at $classTweakerPath for $name")
        accessWidenerPath = classTweakerPath
      } else {
        println("no class tweaker at $classTweakerPath for $name, skipping")
      }

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
          property("datagen.run", name)
          property(
            "datagen.path",
            project.projectDir.toPath().resolve("src/generated/resources/").toAbsolutePath().toString()
          )
          client()
        }
      }
    }

    configurations.all {
      exclude("babric")
    }

    // provide property substitutions
    tasks.withType<ProcessResources> {
      val props = mapOf(
        "version" to project.properties["version"],
        "stationApiMin" to libs.versions.stationapi.min.get(),
        "fabricLoaderMin" to libs.versions.fabric.loader.min.get(),
      )

      props.entries.forEach { (name, value) ->
        inputs.property(name, value)
      }

      filesMatching("fabric.mod.json") {
        expand(props)
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
  }
}

subprojects {
  val libs = rootProject.libs

  project.properties["mavenGroup"]?.let { group = it }
  (project.properties["archivesBaseName"] as String?)?.let { base.archivesName = it }

  // minecraft-specific
  if (!name.startsWith("tool-")) {
    apply(plugin = "com.modrinth.minotaur")

    // maven publishing, for submodules only
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

    val modrinthProjectId = project.properties["modrinthProjectId"] as String?
    val modrinthKey = findProperty("modrinthApiKey") as? String?

    if (modrinthProjectId == null) {
      println("no extra[\"modrinthProjectId\"] for $name, skipping modrinth configuration")
    } else if (modrinthKey == null) {
      println("no modrinthApiKey in ~/.gradle/gradle.properties, skipping modrinth configuration")
    } else {
      modrinth {
        token.set(modrinthKey)
        projectId.set(modrinthProjectId)
        versionNumber.set("${libs.versions.minecraft.get()}-$version")
        versionName.set(version as String)
        versionType.set("release")
        uploadFile.set(tasks.jar)
        changelog.set("Release notes can be found on the [GitHub repository](https://github.com/${gitRepo}/commits/master).")
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
  }
}

// root project
dependencies {
  // include all the subprojects
  subprojects
    .filter { !it.name.startsWith("tool-") }
    .forEach {
      implementation(project(path = ":${it.name}", configuration = "namedElements"))
    }

  // any useful dev environment dependencies here?
}

tasks.register("genSourcesWithRetry") {
  // genSources currently has a consistent failure, requiring it to be run a few times to get past the failing classes
  // (it has an internal cache). try to generate up to 5 times
  group = "fabric"

  doLast {
    val maxRetries = 5
    var currentAttempt = 0
    var errors = mutableListOf<Throwable>()

    while (currentAttempt < maxRetries) {
      try {
        currentAttempt++
        println("attempt $currentAttempt: running genSources...")

        tasks.named("genSources").get().actions.forEach { action ->
          action.execute(tasks.named("genSources").get())
        }

        break // success
      } catch (e: Exception) {
        if (currentAttempt == maxRetries) {
          errors.forEach { println(it) }
          throw e
        } else {
          errors.add(e)
          println("attempt $currentAttempt failed, retrying")
        }
      }
    }
  }
}
