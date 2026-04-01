package pw.tmpim.goodmod.assets

data class VersionInfo(
  val downloads: Downloads
) {
  data class Downloads(
    val client: Download,
    val server: Download?,
  )

  data class Download(
    val sha1: String,
    val size: Long,
    val url: String,
  )
}
