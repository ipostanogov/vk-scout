package vk.scout.wrap.users

sealed abstract class UserField { def value : String }
object ScreenName extends UserField { val value = "screen_name" }

