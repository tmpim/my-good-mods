dependencies {
  implementation(project(path = ":good-utils", configuration = "namedElements"))
  include(project(":good-utils"))

  modCompileOnly(libs.alwaysmoreitems) // for AMI compat in mixin.ami
}

modrinth {
  dependencies {
    required.project("glass-config-api")
  }
}
