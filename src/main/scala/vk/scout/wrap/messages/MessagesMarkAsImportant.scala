package vk.scout.wrap.messages

import vk.scout.wrap.ApiConnector

case class MessagesMarkAsImportant(messageIds: Traversable[Int],
                                   important: Boolean) extends ApiConnector {
  protected[this] val methodName: String = "messages.markAsRead"

  protected[this] def apiParamsMap: Map[String, Option[String]] = Map(
    "message_ids" -> Some(messageIds.mkString(",")),
    "important" -> Some(if (important) "1" else "0")
  )
}
