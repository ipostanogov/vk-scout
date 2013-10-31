package vk.scout.wrap.wall

import vk.scout.wrap.ApiConnector
import vk.scout.helpers._

case class WallGet(ownerId: Int, count: Int = 1000, offset: Int = 0) extends ApiConnector {
  protected[this] final val methodName = "wall.get"

  protected[this] def apiParamsMap: Map[String, Option[String]] = Map(
    "owner_id" -> Option(ownerId.toString),
    "count" -> Option(count.toString),
    "offset" -> Option(offset.toString),
    "filter" -> Some("all")
  )

  def allPosts() : List[PostFromVk] = {
    val messages =  listExtractor[PostFromVk](this.send(), Seq("response", "items"))
    if (messages.isEmpty)
      List.empty
    else
      messages ++ WallGet(ownerId = ownerId, offset = offset + count).allPosts()
  }
}