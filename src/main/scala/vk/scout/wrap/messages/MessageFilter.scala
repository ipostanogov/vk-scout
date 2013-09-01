package vk.scout.wrap.messages

sealed trait MessageFilter { def value: Int }
object Unread extends MessageFilter { val value = 1 }
object NotFromChat extends MessageFilter { val value = 2 }
object FromFriends extends MessageFilter { val value = 4 }
object Important extends MessageFilter { val value = 8 }