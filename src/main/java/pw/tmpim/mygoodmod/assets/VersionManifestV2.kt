package pw.tmpim.mygoodmod.assets

data class VersionManifestV2(
  val versions: List<Version>
) {
  data class Version(
    val id: String,
    val url: String,
    val sha1: String
  )
}
