package pw.tmpim.goodassetfetcher

import com.google.gson.GsonBuilder
import net.fabricmc.loader.api.FabricLoader
import pw.tmpim.goodassetfetcher.GoodAssetFetcher.MOD_ID
import pw.tmpim.goodassetfetcher.GoodAssetFetcher.MOD_VERSION
import pw.tmpim.goodassetfetcher.GoodAssetFetcher.log
import pw.tmpim.goodassetfetcher.mojangapi.VersionInfo
import pw.tmpim.goodassetfetcher.mojangapi.VersionManifestV2
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
internal object AssetFetcherImpl {
  val cacheDir: Path = FabricLoader.getInstance().configDir.resolve("${MOD_ID}/asset-cache")

  private var fetched = false

  private const val VERSION_MANIFEST_URL = "https://piston-meta.mojang.com/mc/game/version_manifest_v2.json"
  private var versionManifest: VersionManifestV2? = null

  private val gson = GsonBuilder().create()
  private val userAgent = "${MOD_ID}/${MOD_VERSION}"

  fun init() {
    cacheDir.toFile().mkdirs()

    fetchVersionManifest()
  }

  @Synchronized
  fun fetchVersionManifest() {
    log.info("Fetching version manifest...")

    val manifest = fetchCached(
      VERSION_MANIFEST_URL,
      "versionManifest-$MOD_VERSION.json" /* refetch on mod update */,
      VersionManifestV2::class.java
    )
    versionManifest = manifest

    log.info("Got ${manifest.versions.size} versions")
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

    if (versionManifest == null) {
      log.error("version manifest not available, skipping assets fetch")
      return
    }

    val filesPerVersion = GoodAssetFetcherRegistryImpl.files
      .groupBy { it.version }

    filesPerVersion.forEach { (version, files) ->
      // check if there are any missing files on disk; if there are, we need to fetch the jar for this version
      if (files.any { cacheDir.resolve(it.destPath).notExists() }) {
        fetchJar(version, files)
      }
    }

    fetched = true
  }

  fun fetchJar(version: String, files: List<GoodAssetFetcherRegistryImpl.ResourceDef>) {
    // fetch the jar url + hash from the version manifest
    val manifest = versionManifest ?: return
    val versionInfoUrl = requireNotNull(manifest.versions.find { it.id == version }) {
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
    val sourceFileLut = files.associateBy { it.sourcePath }

    ByteArrayInputStream(jarBytes.toByteArray()).use { bais ->
      JarInputStream(bais).use { jizz ->
        var entry = jizz.nextEntry

        while (entry != null) {
          val fileMapping = sourceFileLut[entry.name]
          if (fileMapping != null) {
            log.info("extracting ${entry.name}")

            val outFile = cacheDir.resolve(fileMapping.destPath).toFile()
            outFile.parentFile.mkdirs()
            outFile.outputStream().use { jizz.copyTo(it) }

            fileMapping.fetched = true
          }

          entry = jizz.nextEntry
        }
      }
    }

    // error for any files that weren't found
    files.forEach {
      if (!it.fetched) {
        log.error("file ${it.sourcePath} not found in ${it.version}")
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
