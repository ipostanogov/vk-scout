package vk.scout.wrap

import vk.scout.helpers._
import scala.util.Random
import vk.scout.Launcher
import Launcher.Restart
import vk.scout.Launcher

trait ApiConnector extends URLConnector {
  val samePart = "https://api.vk.com/method/"
  val version = "5.0"
  val rand = new Random()

  def pageUrl = samePart + methodName

  val methodName: String

  final def paramsMap: Map[String, Option[String]] =
    apiParamsMap +
      ("access_token" -> Launcher.accessToken, "v" -> Option(version))

  def apiParamsMap: Map[String, Option[String]]

  override def send(): String = {
    val trySend: String = super[URLConnector].send()
    fieldExtractor[ErrorVk](trySend, Seq("error")) match {
      case Some(error) =>
        error.code match {
          // Слишком частые запросы
          case 6 => Thread.sleep(rand.nextInt(100) + 333)
          // Ошибочный код авторизации
          case 5 => Launcher ! Restart
          // В любой непонятной ситуации ложись спать
          case _ => Thread.sleep(5000)
        }
        send()
      case _ => trySend
    }
  }

  implicit def optIntToOptStr(optInt: Option[Int]) = optInt match {
    case Some(value) => Option(value.toString)
    case _ => None
  }
}