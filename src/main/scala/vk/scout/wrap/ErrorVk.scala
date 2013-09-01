package vk.scout.wrap

import argonaut._, Argonaut._

case class ErrorVk(code: Int, message: String) {
  val meaning = code match {
    case 5 => InvalidAccessToken
    case 6 => TooManyRequests
    case _ : Int => OtherErrorCode
  }
}

object ErrorVk {
  implicit def MessageCodecJson: CodecJson[ErrorVk] =
    casecodec2(ErrorVk.apply, ErrorVk.unapply)("error_code", "error_msg")
}

sealed trait ErrorCodeMeaning
object TooManyRequests extends ErrorCodeMeaning
object InvalidAccessToken extends ErrorCodeMeaning
object OtherErrorCode extends ErrorCodeMeaning