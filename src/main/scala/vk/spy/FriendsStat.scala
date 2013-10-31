package vk.spy

import vk.scout.wrap.friends.FriendsGet

object FriendsStat extends App {
  val friends = FriendsGet.getAll("ipostanogov", depth = 2).toList.sortBy(_._2)
  friends.foreach {
    user => println(user._2 + " " + user._1.firstName + " " + user._1.lastName)
  }
  println(friends.size)
}
