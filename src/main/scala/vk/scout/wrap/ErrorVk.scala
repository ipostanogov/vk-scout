package vk.scout.wrap

import argonaut._, Argonaut._

case class ErrorVk(code: Int, message: String)

object ErrorVk {
  implicit def MessageCodecJson: CodecJson[ErrorVk] =
    casecodec2(ErrorVk.apply, ErrorVk.unapply)("error_code", "error_msg")
}