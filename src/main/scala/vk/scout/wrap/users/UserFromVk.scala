package vk.scout.wrap.users

import argonaut._, Argonaut._

case class UserFromVk(id: Int, firstName: String, lastName: String)

object UserFromVk {
  implicit def MessageCodecJson: CodecJson[UserFromVk] =
    casecodec3(UserFromVk.apply, UserFromVk.unapply)("id", "first_name", "last_name")
}