dependencies {
  implementation(project(path = ":good-utils", configuration = "namedElements"))
  include(project(":good-utils"))
}

modrinth {
  dependencies {
    required.project("glass-config-api")
  }
}
