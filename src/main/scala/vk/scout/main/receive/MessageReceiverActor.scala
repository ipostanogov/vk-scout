package vk.scout.main.receive

import scala.actors.Actor
import vk.scout.wrap.messages._
import vk.scout.helpers.{Timer, listExtractor}
import vk.scout.wrap.messages.MessagesGet
import vk.scout.main.display.{ShowMessage, MessageToDisplay}

class MessageReceiverActor(val msgDsplActor: Actor) extends Actor {
  private val displayedMsgs = collection.mutable.Set[Int]()

  private object Check

  def act() {
    def sendToDisplay(msgVk: MessageFromVk) {
      if (displayedMsgs.contains(msgVk.id)) return
      displayedMsgs += msgVk.id
      msgDsplActor ! ShowMessage(MessageToDisplay(msgVk))
    }
    sendToDisplay(MessageFromVk(0, 0, 1, "Авторизация прошла успешно", None, None, ""))
    this ! Check
    loop {
      react {
        case Check =>
          val msgGetResult = MessagesGet(filters = Set(Unread)).send()
          listExtractor[MessageFromVk](msgGetResult, Seq("response", "items")).foreach(sendToDisplay)
          Timer(1000) {
            this ! Check
          }
      }
    }
  }
}

