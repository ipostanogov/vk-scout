package vk.scout.wrap

import vk.scout.helpers._
import scala.util.Random
import vk.scout.Launcher
import scala.language.implicitConversions

trait ApiConnector extends URLConnector {
  private[this] val samePart = "https://api.vk.com/method/"
  private[this] val version = "5.0"
  private[this] val rand = new Random()
  protected[this] val methodName: String
  protected[this] def pageUrl = samePart + methodName

  final protected[this] def paramsMap: Map[String, Option[String]] =
    apiParamsMap +
      ("access_token" -> Launcher.accessToken, "v" -> Option(version))

  protected[this] def apiParamsMap: Map[String, Option[String]]

  override def send(): String = {
    val sendResult: String = super[URLConnector].send()
    fieldExtractor[ErrorVk](sendResult, Seq("error")) match {
      case Some(error) =>
        error.meaning match {
          case TooManyRequests => Thread.sleep(rand.nextInt(100) + 333)
          case InvalidAccessToken => Launcher ! InvalidAccessToken
          // В любой непонятной ситуации ложись спать
          case OtherErrorCode => Thread.sleep(5000)
        }
        send()
      case None => sendResult
    }
  }

  implicit def optIntToOptStr(optInt: Option[Int]) = optInt.map(_.toString)
}