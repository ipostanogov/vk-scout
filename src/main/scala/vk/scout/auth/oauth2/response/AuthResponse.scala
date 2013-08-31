package vk.scout.auth.oauth2.response

import argonaut._, Argonaut._

sealed class AuthResponse

case class SuccessAuthData(accessToken: String, userId: String, expiresIn: String) extends AuthResponse

object SuccessAuthData {
  implicit def SuccessAuthDataCodecJson: CodecJson[SuccessAuthData] =
    casecodec3(SuccessAuthData.apply, SuccessAuthData.unapply)("access_token", "user_id", "expires_in")
}

case class FailAuthData(error: String, errorDescription: String) extends AuthResponse

object FailAuthData {
  implicit def FailAuthDataCodecJson: CodecJson[FailAuthData] =
    casecodec2(FailAuthData.apply, FailAuthData.unapply)("error", "error_description")
}