package vk.scout.wrap.users

sealed abstract class UserField { def value : String }
case object ScreenName extends UserField { val value = "screen_name" }

