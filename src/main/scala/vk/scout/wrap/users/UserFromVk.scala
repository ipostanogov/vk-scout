package vk.scout.wrap.users

import argonaut._, Argonaut._
import vk.scout.helpers._
import vk.scout.wrap.wall.WallGet
import vk.scout.wrap.friends.FriendsGet

case class UserFromVk(id: Int, firstName: String, lastName: String) {
  lazy val friends = UserFromVk.fromId(listExtractor[Int](FriendsGet(id).send(), Seq("response", "items")).map(_.toString))
  lazy val postsOnWall = WallGet(ownerId = id).allPosts()
}

object UserFromVk {
  implicit def MessageCodecJson: CodecJson[UserFromVk] =
    casecodec3(UserFromVk.apply, UserFromVk.unapply)("id", "first_name", "last_name")

  def fromId(id: String) = {
    val answerForAllUsersRequest = UsersGet(userScreenNames = Set(id)).send()
    listExtractor[UserFromVk](answerForAllUsersRequest, Seq("response")) match {
      case List(h) => h
      case _ => throw new IllegalArgumentException
    }
  }

  def fromId(ids: Seq[String]): List[UserFromVk] = {
    if (ids.size < 200) {
      val answerForAllUsersRequest = UsersGet(userScreenNames = ids.toSet).send()
      listExtractor[UserFromVk](answerForAllUsersRequest, Seq("response"))
    }
    else {
      val split = ids.splitAt(175)
      fromId(split._1) ::: fromId(split._2)
    }
  }
}