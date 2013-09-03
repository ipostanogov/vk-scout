import AssemblyKeys._ // put this at the top of the file

assemblySettings

name := "vk-scout"

version := "0.1.2"

scalaVersion := "2.10.2"

javaHome := {
  var s = System.getenv("JAVA_HOME")
  //
  val dir = new File(s)
  if (!dir.exists) {
    throw new RuntimeException( "No JDK found - try setting 'JAVA_HOME'." )
  }
  //
  Some(dir)  // 'sbt' 'javaHome' value is ': Option[java.io.File]'
}

unmanagedJars in Compile <+= javaHome map { jh /*: Option[File]*/ =>
  val dir: File = jh.getOrElse(null)    // unSome
  //
  val jfxJar = new File(dir, "/jre/lib/jfxrt.jar")
  if (!jfxJar.exists) {
    throw new RuntimeException( "JavaFX not detected (needs Java runtime 7u06 or later): "+ jfxJar.getPath )  // '.getPath' = full filename
  }
  Attributed.blank(jfxJar)
}

mainClass in (Compile, run)  := Some("vk.scout.Launcher")

mainClass in (Compile, packageBin)  := Some("vk.scout.Launcher")

mainClass in assembly     := Some("vk.scout.Launcher")