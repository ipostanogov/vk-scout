package vk.scout.wrap

import argonaut._, Argonaut._

case class ErrorVk(code: Int, message: String) {
  val meaning = code match {
    case 5 => InvalidAccessToken
    case 6 => TooManyRequests
    case 15 => UserDeactivated
    case 113 => InvalidUserId
    case _ : Int => OtherErrorCode
  }
}

object ErrorVk {
  implicit def MessageCodecJson: CodecJson[ErrorVk] =
    casecodec2(ErrorVk.apply, ErrorVk.unapply)("error_code", "error_msg")
}

sealed trait ErrorCodeMeaning
object InvalidAccessToken extends ErrorCodeMeaning
object InvalidUserId extends ErrorCodeMeaning
object OtherErrorCode extends ErrorCodeMeaning
object TooManyRequests extends ErrorCodeMeaning
object UserDeactivated extends ErrorCodeMeaning