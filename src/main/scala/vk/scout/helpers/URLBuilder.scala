package vk.scout.helpers

import java.net.{UnknownHostException, SocketException, URL}
import scala.io.Source

trait URLBuilder {
  protected[this] def pageUrl: String

  protected[this] def paramsMap: Map[String, Option[String]]

  protected[this] def paramsStr = (for {
    (key, plainValue) <- paramsMap
    if plainValue.isDefined
    encValue = java.net.URLEncoder.encode(plainValue.get, "utf8")
  } yield s"$key=$encValue").mkString("&")

  def getUrl = pageUrl + "?" + paramsStr
}

trait URLConnector extends URLBuilder {
  def send() : String = {
    val connection = new URL(getUrl).openConnection
    try {
    Source.fromInputStream(connection.getInputStream, "utf8").getLines().mkString("")
    }
    catch {
      case _ : SocketException | _ : UnknownHostException => Thread.sleep(1000); send()
    }
  }
}