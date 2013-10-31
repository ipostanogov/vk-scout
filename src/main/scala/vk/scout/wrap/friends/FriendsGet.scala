package vk.scout.wrap.friends

import vk.scout.wrap.ApiConnector
import vk.scout.wrap.users.UserFromVk

case class FriendsGet(userId: Int) extends ApiConnector {
  protected[this] val methodName: String = "friends.get"
  protected[this] def apiParamsMap: Map[String, Option[String]] = Map("user_id" -> Some(userId.toString))
}

object FriendsGet {
  def getAll(user: UserFromVk, depth: Int): Map[UserFromVk, Int] = {
    var friendsWithRepeatsCount = user.friends.map((_, 1)).toMap.withDefaultValue(0)
    if (depth > 1) {
      friendsWithRepeatsCount.keys.map(user => getAll(user, depth - 1)).foreach {
        for ((friendOfFriend, count) <- _)
          friendsWithRepeatsCount = friendsWithRepeatsCount.updated(friendOfFriend, friendsWithRepeatsCount(friendOfFriend) + count)
      }
    }
    friendsWithRepeatsCount
  }

  def getAll(id: String, depth: Int): Map[UserFromVk, Int] = getAll(UserFromVk.fromId(id), depth)
}