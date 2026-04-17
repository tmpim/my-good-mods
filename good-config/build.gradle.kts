dependencies {
  implementation(project(path = ":good-utils", configuration = "namedElements"))
  include(project(":good-utils"))

  modImplementation(libs.glassnetworking) // https://github.com/Glass-Series/Glass-Networking

  // implementation(libs.datafixerupper)
  // include(libs.datafixerupper)

  implementation(libs.jtoml)
  include(libs.jtoml)
}

modrinth {
  dependencies {
    required.project("glass-networking")
  }
}

sourceSets {
  test {
    // compileClasspath += sourceSets["main"].compileClasspath + sourceSets["main"].output
    // runtimeClasspath += sourceSets["main"].runtimeClasspath + sourceSets["main"].output
  }
}

loom {
  runs {
    register("runTestmodClient") {
      source("test")
      client()
    }

    register("runTestmodServer") {
      source("test")
      server()
    }
  }
}
