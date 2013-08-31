package vk.scout.wrap.messages

import vk.scout.wrap.ApiConnector

case class MessagesMarkAsRead(messageIds: Traversable[Int],
                              senderId: Int,
                              startMid: Option[Int] = None) extends ApiConnector {
  val methodName: String = "messages.markAsRead"

  def apiParamsMap: Map[String, Option[String]] = Map(
    "message_ids" -> Some(messageIds.mkString(",")),
    "user_id" -> Some(senderId.toString),
    "start_mid" -> startMid
  )
}
