package vk.scout.helpers

object MagicHeapCleaner {
  // I don't know why, but program generates a lot of objects.
  // And needs implicit recommendation to GC
  def run() {
    Timer(5000) {
      System.gc()
      run()
    }
  }
}
