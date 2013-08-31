package vk.scout.wrap.status

import vk.scout.wrap.ApiConnector

case class StatusSet(text: Option[String] = None, audio: Option[String] = None) extends ApiConnector {
  final val methodName = "status.set"

  def apiParamsMap = Map("text" -> text, "audio" -> audio)
}