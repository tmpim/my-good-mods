package pw.tmpim.mygoodmod.assets

import com.google.gson.GsonBuilder
import net.fabricmc.loader.api.FabricLoader
import pw.tmpim.mygoodmod.MyGoodMod.MOD_ID
import pw.tmpim.mygoodmod.MyGoodMod.MOD_VERSION
import pw.tmpim.mygoodmod.MyGoodMod.log
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Path
import java.security.MessageDigest
import java.util.jar.JarInputStream
import kotlin.io.path.bufferedReader
import kotlin.io.path.exists
import kotlin.io.path.notExists
import kotlin.io.path.outputStream

// based on hugeblank/clustersback (MIT licensed)
// https://github.com/hugeblank/clustersback/blob/main/src/main/java/dev/hugeblank/clustersback/MinecraftJarGetter.java
object AssetFetcher {
  val cacheDir: Path = FabricLoader.getInstance().configDir.resolve("${MOD_ID}/asset-cache")

  private var fetched = false

  private const val VERSION_MANIFEST_URL = "https://piston-meta.mojang.com/mc/game/version_manifest_v2.json"
  private lateinit var versionManifest: VersionManifestV2

  private val gson = GsonBuilder().create()
  private val userAgent = "${MOD_ID}/${MOD_VERSION}"

  fun init() {
    cacheDir.toFile().mkdirs()

    fetchVersionManifest()
  }

  @Synchronized
  fun fetchVersionManifest() {
    log.info("Fetching version manifest...")
    val balls = fetchCached(
      VERSION_MANIFEST_URL,
      "versionManifest-$MOD_VERSION.json" /* refetch on mod update */,
      VersionManifestV2::class.java
    )
    log.info("look at my balls: ${balls}")
    versionManifest = balls
    log.info("Got ${versionManifest.versions.size} versions")
  }

  @Synchronized
  fun fetchVersionInfo(version: String, url: String): VersionInfo {
    log.info("Fetching version info for $version...")
    return fetchCached(
      url,
      "$version-$MOD_VERSION.json" /* refetch on mod update */,
      VersionInfo::class.java
    )
  }

  @Synchronized
  fun fetchAssets() {
    if (fetched) return

    val filesPerVersion = GoodResources.files
      .groupBy { it.version }

    filesPerVersion.forEach { (version, files) ->
      // check if there are any missing files on disk; if there are, we need to fetch the jar for this version
      if (files.any { cacheDir.resolve(it.destPath).notExists() }) {
        fetchJar(version, files)
      }
    }

    fetched = true
  }

  fun fetchJar(version: String, files: List<GoodResources.ResourceDef>) {
    // fetch the jar url + hash from the version manifest
    val versionInfoUrl = requireNotNull(versionManifest.versions.find { it.id == version }) {
      "version $version not found!"
    }
    val versionInfo = fetchVersionInfo(version, versionInfoUrl.url)
    val (sha1, size, url) = versionInfo.downloads.client

    // download the jar to memory and hash it
    val client = HttpClient.newBuilder().build()
    val res = client.send(HttpRequest.newBuilder()
      .uri(URI.create(url))
      .header("User-Agent", userAgent)
      .build(), HttpResponse.BodyHandlers.ofInputStream())

    val digest = MessageDigest.getInstance("SHA-1")
    val jarBytes = ByteArrayOutputStream(size.toInt())

    res.body().use { input ->
      val buffer = ByteArray(8192)
      var read: Int

      while (input.read(buffer).also { read = it } != -1) {
        digest.update(buffer, 0, read)
        jarBytes.write(buffer, 0, read)
      }
    }

    val gotSha1 = digest.digest().joinToString("") { "%02x".format(it) }
    check(gotSha1.equals(sha1, ignoreCase = true)) { "sha1 mismatch for $version client.jar" }

    // extract the files we need and write to disk
    val sourceFileLut = files.mapTo(mutableSetOf()) { it.sourcePath }

    ByteArrayInputStream(jarBytes.toByteArray()).use { bais ->
      JarInputStream(bais).use { jizz ->
        var entry = jizz.nextEntry

        while (entry != null) {
          if (entry.name in sourceFileLut) {
            log.info("extracting ${entry.name}")

            val outFile = cacheDir.resolve(entry.name).toFile()
            outFile.parentFile.mkdirs()
            outFile.outputStream().use { jizz.copyTo(it) }
          }

          entry = jizz.nextEntry
        }
      }
    }

    log.info("finished processing $version")
  }

  @Synchronized
  private fun <T>fetchCached(url: String, cacheKey: String, dataClass: Class<T>): T {
    val file = cacheDir.resolve(cacheKey)

    if (!file.exists()) {
      log.info("not cached, fetching $url for $cacheKey")
      fetchUrl(url).use { input ->
        file.outputStream().use { input.copyTo(it) }
      }
    } else {
     log.info("using cached file for $cacheKey")
    }

    return file.bufferedReader().use {
      requireNotNull(gson.fromJson(it, dataClass)) { "failed to parse $url for $cacheKey" }
    }
  }

  private fun fetchUrl(url: String): InputStream {
    val client = HttpClient.newBuilder().build()
    val res = client.send(HttpRequest.newBuilder()
      .uri(URI.create(url))
      .header("User-Agent", userAgent)
      .header("Accept", "application/json")
      .build(), HttpResponse.BodyHandlers.ofInputStream())
    return res.body()
  }
}
