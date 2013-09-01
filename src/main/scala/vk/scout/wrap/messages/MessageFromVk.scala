package vk.scout.wrap.messages

import argonaut._, Argonaut._

case class MessageFromVk(id: Int, date: Int, userId: Int, body: String, emoji: Option[Int]) {
  lazy val bodyWithImg =
    if (body.isEmpty)
      "<html><img src='http://savepic.su/3143652.png'></html>"
    else if (!emoji.isDefined)
      body
    else
      MessageFromVk.replaceEmojiCodesWithImages(body)
}

object MessageFromVk {
  private[this] val emojiToDisplay: List[Int] = List(0x263A, 0xD83DDE0A, 0xD83DDE03, 0xD83DDE09, 0xD83DDE06, 0xD83DDE1C, 0xD83DDE0B, 0xD83DDE0D, 0xD83DDE0E, 0xD83DDE12, 0xD83DDE0F, 0xD83DDE14, 0xD83DDE22, 0xD83DDE2D, 0xD83DDE29, 0xD83DDE28, 0xD83DDE10, 0xD83DDE0C, 0xD83DDE20, 0xD83DDE21, 0xD83DDE07, 0xD83DDE30, 0xD83DDE32, 0xD83DDE33, 0xD83DDE37, 0xD83DDE1A, 0xD83DDE08, 0x2764, 0xD83DDC4D, 0xD83DDC4E, 0x261D, 0x270C, 0xD83DDC4C, 0x26BD, 0x26C5, 0xD83CDF1F, 0xD83CDF4C, 0xD83CDF7A, 0xD83CDF7B, 0xD83CDF39, 0xD83CDF45, 0xD83CDF52, 0xD83CDF81, 0xD83CDF82, 0xD83CDF84, 0xD83CDFC1, 0xD83CDFC6, 0xD83DDC0E, 0xD83DDC0F, 0xD83DDC1C, 0xD83DDC2B, 0xD83DDC2E, 0xD83DDC03, 0xD83DDC3B, 0xD83DDC3C, 0xD83DDC05, 0xD83DDC13, 0xD83DDC18, 0xD83DDC94, 0xD83DDCAD, 0xD83DDC36, 0xD83DDC31, 0xD83DDC37, 0xD83DDC11, 0x23F3, 0x26BE, 0x26C4, 0x2600, 0xD83CDF3A, 0xD83CDF3B, 0xD83CDF3C, 0xD83CDF3D, 0xD83CDF4A, 0xD83CDF4B, 0xD83CDF4D, 0xD83CDF4E, 0xD83CDF4F, 0xD83CDF6D, 0xD83CDF37, 0xD83CDF38, 0xD83CDF46, 0xD83CDF49, 0xD83CDF50, 0xD83CDF51, 0xD83CDF53, 0xD83CDF54, 0xD83CDF55, 0xD83CDF56, 0xD83CDF57, 0xD83CDF69, 0xD83CDF83, 0xD83CDFAA, 0xD83CDFB1, 0xD83CDFB2, 0xD83CDFB7, 0xD83CDFB8, 0xD83CDFBE, 0xD83CDFC0, 0xD83CDFE6, 0xD83DDC00, 0xD83DDC0C, 0xD83DDC1B, 0xD83DDC1D, 0xD83DDC1F, 0xD83DDC2A, 0xD83DDC2C, 0xD83DDC2D, 0xD83DDC3A, 0xD83DDC3D, 0xD83DDC2F, 0xD83DDC5C, 0xD83DDC7B, 0xD83DDC14, 0xD83DDC23, 0xD83DDC24, 0xD83DDC40, 0xD83DDC42, 0xD83DDC43, 0xD83DDC46, 0xD83DDC47, 0xD83DDC48, 0xD83DDC51, 0xD83DDC60, 0xD83DDCA1, 0xD83DDCA3, 0xD83DDCAA, 0xD83DDCAC, 0xD83DDD14, 0xD83DDD25)

  private def replaceEmojiCodesWithImages(s: String): String = replaceEmojiCodesWithImages(s, emojiToDisplay)

  private[this] def replaceEmojiCodesWithImages(str: String, emojies: Traversable[Int]): String = {
    emojies match {
      case h :: t =>
        lazy val img = "<img src='http://vk.com/images/emoji/" + "%x".format(h).toUpperCase + ".png'>"
        val pattern = (if ((h >> 16) != 0) (h >> 16).toChar + "" else "") + (h & 0xFFFF).toChar
        val newStr = str.replace(pattern, img)
        replaceEmojiCodesWithImages(newStr, t)
      case Nil => s"<html>$str</html>"
    }
  }

  implicit def MessageCodecJson: CodecJson[MessageFromVk] =
    casecodec5(MessageFromVk.apply, MessageFromVk.unapply)("id", "date", "user_id", "body", "emoji")
}