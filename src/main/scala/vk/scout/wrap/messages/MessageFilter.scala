package vk.scout.wrap.messages

sealed abstract class MessageFilter { def value: Int }
case object Unread extends MessageFilter { val value = 1 }
case object NotFromChat extends MessageFilter  { val value = 2 }
case object FromFriends extends MessageFilter  { val value = 4 }
case object Important extends MessageFilter  { val value = 8 }