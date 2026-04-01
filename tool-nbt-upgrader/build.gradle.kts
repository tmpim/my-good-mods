import sun.jvmstat.monitor.MonitoredVmUtil.mainClass

plugins {
  application
}

dependencies {
  implementation(libs.nbt)
}

application {
  mainClass.set("pw.tmpim.nbtupgrader.MainKt")
}
