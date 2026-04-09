dependencies {
  implementation(project(path = ":good-utils", configuration = "namedElements"))
  include(project(":good-utils"))

  modImplementation(libs.glassnetworking) // https://github.com/Glass-Series/Glass-Networking

  modCompileOnly(libs.alwaysmoreitems) // for AMI compat in GoodFlagsAMIPlugin
  modCompileOnly("maven.modrinth:entityculling:1.6.3-b1.7.3") // for whitelisting in GoodFlagsEntityCullingPlugin
}

modrinth {
  dependencies {
    required.project("glass-config-api")
    required.project("glass-networking")
  }
}
