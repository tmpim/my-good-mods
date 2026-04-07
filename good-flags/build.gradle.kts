dependencies {
  implementation(project(path = ":good-utils", configuration = "namedElements"))
  include(project(":good-utils"))

  // TODO: remove dependency on Glass Networking when gcapi is updated
  modImplementation(libs.glassnetworking) // https://github.com/Glass-Series/Glass-Networking

  modCompileOnly(libs.alwaysmoreitems) // for AMI compat in GoodFlagsAMIPlugin
}

modrinth {
  dependencies {
    required.project("glass-config-api")
    required.project("glass-networking")
  }
}
