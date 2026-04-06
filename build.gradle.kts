@file:Suppress("UnstableApiUsage")

import groovy.json.JsonSlurper
import groovy.util.Node
import groovy.util.NodeList
import net.fabricmc.loom.task.GenerateSourcesTask
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.file.FileVisitResult
import java.nio.file.Files
import kotlin.io.path.*

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
    apiVersion = KotlinVersion.KOTLIN_2_3
    languageVersion = KotlinVersion.KOTLIN_2_3
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

        // verify the class tweaker is actually referenced in fabric.mod.json
        val fabricModJson = getFabricModJson()
        if (fabricModJson != null) {
          val definedPath = checkNotNull(fabricModJson["accessWidener"] as? String) {
            """
              |project $name has a class tweaker at $classTweakerPath, but it is missing from fabric.mod.json!
              |
              |add the following line to fabric.mod.json, usually above "depends":
              |    "accessWidener": "${classTweakerPath.name}",
            """.trimMargin().trim()
          }

          check(definedPath == classTweakerPath.name) {
            """
              |project $name has a class tweaker at $classTweakerPath, but it is defined differently in fabric.mod.json!
              |
              |got:
              |    "accessWidener": "$definedPath",
              |
              |expected:
              |    "accessWidener": "${classTweakerPath.name}",
            """.trimMargin().trim()
          }
        }
      } else {
        println("no class tweaker at $classTweakerPath for $name, skipping")
      }

      runs.configureEach {
        ideConfigGenerated(false) // disable IDEA run configurations
        vmArgs("-Dmixin.debug.export=true")
      }
    }

    configurations.all {
      exclude("babric")
    }

    // provide property substitutions
    tasks.withType<ProcessResources> {
      val props = mapOf(
        "version" to project.properties["version"],
        "javaVersion" to libs.versions.java.get(),
        "stationApiMin" to libs.versions.stationapi.min.get(),
        "fabricLoaderMin" to libs.versions.fabric.loader.min.get(),
      )

      props.entries.forEach { (name, value) ->
        inputs.property(name, value)
      }

      // IDEA will emit "Cannot resolve resource filtering of MatchingCopyAction." but this can be safely ignored
      // https://youtrack.jetbrains.com/issue/IDEA-296490
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

    configurations.namedElements.get().extendsFrom(configurations.implementation.get())

    loom {
      runs {
        // add datagen task if the subproject has a data entrypoint
        val fabricModJson = getFabricModJson()
        if (fabricModJson != null && (fabricModJson["entrypoints"] as Map<*, *>?)?.containsKey("data") == true) {
          println("found data entrypoint in $name, registering runData task")

          register("data") {
            client()
            property("datagen.run", project.name)
            property(
              "datagen.path",
              project.projectDir.toPath().resolve("src/generated/resources/").toAbsolutePath().toString()
            )
          }
        }

        // remove client/server tasks for subprojects
        findByName("client")?.let(::remove)
        findByName("server")?.let(::remove)
      }
    }

    // disable confusing/broken tasks for subprojects
    tasks.configureEach {
      if (name in setOf("runClient", "runServer", "genSourcesWithCfr")) {
        enabled = false
        group = null // hide the task in the 'other' group
      }
    }

    // maven publishing, for submodules only
    publishing {
      publications {
        register("mavenJava", MavenPublication::class) {
          from(components["java"])

          pom.withXml {
            (asNode().get("dependencies") as? NodeList)?.getAt("dependency")?.forEach {
              val node = it as Node
              val artifactId = (node.get("artifactId") as? NodeList)?.text() ?: ""

              // exclude these artifact ids from the published pom
              val excluded = listOf("StationAPI")

              if (artifactId in excluded) {
                node.parent().remove(node)
              }
            }
          }
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
        token = modrinthKey
        projectId = modrinthProjectId
        versionNumber = "${libs.versions.minecraft.get()}-$version"
        versionName = version as String
        versionType = "release"
        uploadFile.set(tasks.remapJar)
        changelog = "Release notes can be found on the [GitHub repository](https://github.com/${gitRepo}/commits/master)."
        gameVersions.add(libs.versions.minecraft.get())
        loaders.add("fabric")
        loaders.add("babric")

        syncBodyFrom.set(provider {
          val readmeFile = file("README.md")
          val readmeDir = readmeFile.parentFile
            .relativeTo(rootProject.projectDir)
            .path
            .let { if (it.isEmpty()) "" else "$it/" }

          readmeFile.readText()
            // replace links to (./LICENSE) to the project's root license file
            .replace(Regex("""\([\.\/]+LICENSE\)"""), "(https://github.com/${gitRepo}/blob/HEAD/LICENSE)")
            // replace relative image URLs
            .replace(Regex("""\((?!https?://)(/?images/.+?\.png)\)""")) { match ->
              val imagePath = match.groupValues[1].trimStart('/')
              "(https://raw.githubusercontent.com/${gitRepo}/HEAD/${readmeDir}${imagePath})"
            }
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
}

loom {
  runs {
    named("server") {
      runDir = "runServer"
    }
  }
}

afterEvaluate {
  // disable confusing/broken tasks for the root project
  setOf("runData", "genSources", "genSourcesWithVineflower").forEach { name ->
    tasks.findByName(name)?.let {
      it.enabled = false
      it.group = null // hide the task in the 'other' group
    }
  }

  // remove all other root genSources tasks
  tasks.withType<GenerateSourcesTask> {
    enabled = false
    group = null // hide the task in the 'other' group
  }

  // TODO HACK: add subproject resources to the root run configuration
  val extraResourceDirs = subprojects
    .filter { !it.name.startsWith("tool-") }
    .flatMap { sub ->
      sub.sourceSets.flatMap { ss ->
        ss.resources.srcDirs.filter { it.exists() }
      }
    }

  // root runClient
  tasks.named<JavaExec>("runClient") {
    classpath = files(extraResourceDirs) + classpath
  }
}

tasks.register("generateMod") {
  doLast {
    // CONFIGURATION
    var templateDir = projectDir.resolve("good-mod-template")
    check(templateDir.exists()) { "good-mod-template dir is missing!" }

    // prompt for info
    println("== mod settings ==")

    println("mod name (human friendly, 1-32 characters, starts with 'Good ', e.g. 'Good Food') [REQUIRED]:")
    val modName = readInput()
    check(modName.length in 1..32) { "mod name should be between 1 and 32 characters" }

    var modId = modName.toKebabCase()
    println("mod id (lowercase alphanumeric) [press enter to use $modId]:")
    modId = readInput().takeIf { it.isNotEmpty() } ?: modId
    check(modId.matches("^[a-z0-9-]+$".toRegex())) { "$modId is not a valid mod ID" }

    var archivesBaseName = modId
    println("maven archive base name (snake_case) [press enter to use $archivesBaseName]:")
    archivesBaseName = readInput().takeIf { it.isNotEmpty() } ?: archivesBaseName

    var packageNameBase = modId.replace("-", "")
    println("package name base (lowercase) [press enter to use $packageNameBase]:")
    packageNameBase = readInput().takeIf { it.isNotEmpty() } ?: packageNameBase

    var classNameBase = modName.toPascalCase()
    println("class name base (PascalCase) [press enter to use $classNameBase]:")
    classNameBase = readInput().takeIf { it.isNotEmpty() } ?: classNameBase

    // check the mod-id dir doesn't exist yet
    var modDir = projectDir.resolve(modId)
    check(!modDir.exists()) { "$modId already exists: $modDir" }

    println("== confirmation ==")
    println("making a mod with the following settings:\n")
    println("  - Mod Name: $modName")
    println("  - Mod ID  : $modId")
    println("  - Archive : $archivesBaseName")
    println("  - Package : $packageNameBase")
    println("  - Class   : $classNameBase")
    println("  - Mod Dir : $modDir")
    println("\nare you sure? Y/n")

    var confirmation = readInput().trim().lowercase()
    check(confirmation.isEmpty() || confirmation == "y") { "aborted" }

    // SETUP
    val textExtensions = setOf("kt", "kts", "java", "json", "classtweaker", "properties", "md", "lang")
    val ignoreDirs = setOf(".gradle", "build", "run", ".idea", ".kotlin")

    fun replacer(str: String) = str
      .replace("Good Mod Template", modName)
      .replace("GoodModTemplate", classNameBase)
      .replace("good-mod-template", modId)
      .replace("goodmodtemplate", packageNameBase)

    modDir.mkdirs()

    val templateVisitor = fileVisitor {
      onPreVisitDirectory { dir, _ ->
        when (dir.name) {
          in ignoreDirs -> {
            println("skipping ${dir.name}")
            FileVisitResult.SKIP_SUBTREE
          }
          else -> {
            val srcFile = dir.toFile().toRelativeString(projectDir)
            val dstFile = projectDir.resolve(replacer(srcFile))

            println("creating directory $dstFile")
            dstFile.mkdirs()

            FileVisitResult.CONTINUE
          }
        }
      }

      onVisitFile { file, _ ->
        val srcFile = file.toFile().toRelativeString(projectDir)
        val dstFile = projectDir.resolve(replacer(srcFile))

        if (dstFile.exists()) {
          println("warning: $dstFile already exists! not touching it")
        } else {
          println("creating file $srcFile -> $dstFile")

          if (file.extension in textExtensions) {
            dstFile.writeText(replacer(file.readText()))
          } else {
            file.copyTo(dstFile.toPath())
          }
        }

        FileVisitResult.CONTINUE
      }
    }

    Files.walkFileTree(templateDir.toPath(), templateVisitor)

    println("\n\ndone!! now add the mod to settings.gradle.kts and reload the project\n\n")
  }
}

fun readInput(): String = BufferedReader(InputStreamReader(System.`in`)).readLine()

fun String.toSnakeCase() = this
  .replace("([a-z])([A-Z]+)".toRegex(), "$1_$2")
  .replace("\\s+".toRegex(), "_")
  .replace("[^A-Za-z0-9_]".toRegex(), "")
  .lowercase()

fun String.toKebabCase() = this
  .toSnakeCase()
  .replace("_", "-")

fun String.toPascalCase() = this
  .split("[^A-Za-z0-9]+".toRegex())
  .joinToString("") { word -> word.lowercase().replaceFirstChar { it.uppercase() } }

fun String.toCamelCase() = this
  .toPascalCase()
  .replaceFirstChar { it.lowercase() }

fun Project.getFabricModJson(): Map<*, *>? =
  project.projectDir
    .resolve("src/main/resources/fabric.mod.json")
    .takeIf { it.exists() }
    ?.let { JsonSlurper().parse(it) as Map<*, *> }
