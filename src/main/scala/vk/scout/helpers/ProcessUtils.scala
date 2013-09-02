package vk.scout.helpers

import java.io.File
import scala.collection.JavaConverters._

object ProcessUtils {
  private val startHeapSizeInMb = 15
  private val maxHeapSizeInMb = 100
  private val javaBin = {
    val path = "java"
    try {
      new ProcessBuilder(path).start()
      Some(path)
    }
    catch {
      case _: Throwable => None
    }
  }

  private val currentJar = new File(getClass.getProtectionDomain.getCodeSource.getLocation.toURI)
  val wasRunFromJar = currentJar.getName.endsWith(".jar")

  def RestartIfHeapSizeNeedsReducing() {
    if (javaBin.isDefined && wasRunFromJar && (Runtime.getRuntime.maxMemory() > ((maxHeapSizeInMb + 1) << 20)))
      tryRestartProgramOrExit(0)
  }

  def tryRestartProgramOrExit(code: Int) {
    javaBin match {
      case Some(path) =>
        val runCmd = Seq(path, "-Xms" + startHeapSizeInMb + "M", "-Xmx" + maxHeapSizeInMb + "M", "-jar", currentJar.getPath)
        new ProcessBuilder(runCmd.asJava).start
      case None =>
    }
    System.exit(code)
  }
}
