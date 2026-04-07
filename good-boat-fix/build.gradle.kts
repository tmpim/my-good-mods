dependencies {
  modCompileOnly(libs.unitweaks)
}

modrinth {
  dependencies {
    required.project("glass-config-api")
  }
}
