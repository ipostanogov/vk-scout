package vk.scout

import argonaut._, Argonaut._

package object helpers {
  def listExtractor[T: DecodeJson](jsonStr: String, downFields: Seq[String]) = {
    // with help from gpampara @ http://pastebin.com/bgjnkPe3
    def decodeResultOfListToList(value: Option[DecodeResult[List[T]]]): List[T] =
      value.map(_.getOr(List.empty)) getOrElse List.empty

    def deeper(curs: Option[Cursor], fields: Seq[String]): Option[DecodeResult[List[T]]] = {
      if (fields.isEmpty)
        curs.map(_.focus.jdecode[List[T]])
      else
        curs.flatMap(x => deeper(x.downField(fields.head), fields.tail))
    }
    jsonStr.parse.fold(sys.error, json => decodeResultOfListToList(deeper(Option(json.cursor), downFields)))
  }

  def fieldExtractor[T: DecodeJson](jsonStr: String, downFields: Seq[String]) = {
    def decodeResultOfTToOptionT(value: Option[DecodeResult[T]]): Option[T] =
      value.map(_.toOption) getOrElse None

    def deeper(curs: Option[Cursor], fields: Seq[String]): Option[DecodeResult[T]] = {
      if (fields.isEmpty)
        curs.map(_.focus.jdecode[T])
      else
        curs.flatMap(x => deeper(x.downField(fields.head), fields.tail))
    }
    jsonStr.parse.fold(sys.error, json => decodeResultOfTToOptionT(deeper(Option(json.cursor), downFields)))
  }

  object Timer {
    def apply(interval: Int, repeats: Boolean = false)(op: => Unit) {
      val timeOut = new javax.swing.AbstractAction() {
        def actionPerformed(e: java.awt.event.ActionEvent) = op
      }
      val t = new javax.swing.Timer(interval, timeOut)
      t.setRepeats(repeats)
      t.start()
    }
  }
}
