package vk.scout.wrap.messages

import vk.scout.wrap.ApiConnector

case class MessagesGet(out: Option[Int] = None,
                       offset: Option[Int] = None,
                       count: Option[Int] = None,
                       timeOffset: Option[Int] = Option(0),
                       filters: Set[MessageFilter] = Set(Unread),
                       previewLength: Option[Int] = Option(0),
                       lastMessageId: Option[Int] = None) extends ApiConnector {
  protected[this] final val methodName = "messages.get"

  def apiParamsMap: Map[String, Option[String]] = for ((k, v) <- apiIntParamsMap) yield (k, optIntToOptStr(v))

  def apiIntParamsMap: Map[String, Option[Int]] = Map(
    "out" -> out,
    "offset" -> offset,
    "count" -> count,
    "time_offset" -> timeOffset,
    "filters" -> Some(filters.map(_.value).sum),
    "preview_length" -> previewLength,
    "last_message_id" -> lastMessageId
  )
}
