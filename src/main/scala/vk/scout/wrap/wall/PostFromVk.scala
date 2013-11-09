package vk.scout.wrap.wall

import argonaut._, Argonaut._
import vk.scout.wrap.users.{UsersGet, UserFromVk}
import vk.scout.helpers._

case class PostFromVk(id: Int, wallOwnerId: Int, authorId: Int, date: Int, text : String)    {
  lazy val normDate = new java.util.Date(date.toLong * 1000)
  lazy val author = {
    listExtractor[UserFromVk](UsersGet(userIntIds = Set(authorId)).send(),Seq("response")) match {
      case List(h) => h
      case _ => throw new Exception
    }
  }

  lazy val formatted = author.firstName + " " + author.lastName + " @ " + normDate + " : " + text
}

object PostFromVk {
  implicit def MessageCodecJson: CodecJson[PostFromVk] =
    casecodec5(PostFromVk.apply, PostFromVk.unapply)("id", "to_id", "from_id", "date", "text")
}