dependencies {
  implementation(project(path = ":good-asset-fetcher", configuration = "namedElements"))
}

modrinth {
  dependencies {
    required.project("glass-config-api")
    required.project("good-asset-fetcher")
  }
}
