dependencies {
  implementation(project(path = ":good-utils", configuration = "namedElements"))
  include(project(":good-utils"))

  modImplementation(libs.glassnetworking) // https://github.com/Glass-Series/Glass-Networking
}

modrinth {
  dependencies {
    required.project("glass-config-api")
    required.project("glass-networking")
  }
}
