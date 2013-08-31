package vk.scout.config

import argonaut._, Argonaut._
import vk.scout.auth.rsa.RSATool

case class EncLoginPassword(login: Array[Byte], password: Array[Byte]) {
  def withString = EncLoginPasswordInString(login.mkString(","), password.mkString(","))

  def decode(rsaTool: RSATool) = LoginPassword(rsaTool.decrypt(login), rsaTool.decrypt(password))
}

case class EncLoginPasswordInString(login: String, password: String) {
  def withBytes = EncLoginPassword(login.split(",").map(_.toByte), password.split(",").map(_.toByte))
}

object EncLoginPasswordInString {
  implicit def EncLoginPasswordInStringCodecJson: CodecJson[EncLoginPasswordInString] =
    casecodec2(EncLoginPasswordInString.apply, EncLoginPasswordInString.unapply)("login", "password")
}

case class LoginPassword(login: String, password: String) {
  def encode(rsaTool: RSATool) = EncLoginPassword(rsaTool.encrypt(login), rsaTool.encrypt(password))
}

object LoginPassword {
  implicit def LoginPasswordCodecJson: CodecJson[LoginPassword] =
    casecodec2(LoginPassword.apply, LoginPassword.unapply)("login", "password")
}

case class RSAKeysFileNames(publicKeyFileName: String, privateKeyFileName: String)

object RSAKeysFileNames {
  implicit def RSAKeysFileNamesCodecJson: CodecJson[RSAKeysFileNames] =
    casecodec2(RSAKeysFileNames.apply, RSAKeysFileNames.unapply)("public", "private")
}