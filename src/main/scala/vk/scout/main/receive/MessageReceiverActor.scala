package vk.scout.main.receive

import scala.actors.Actor
import vk.scout.wrap.messages._
import vk.scout.helpers.{Timer, listExtractor}
import vk.scout.wrap.messages.MessagesGet
import vk.scout.wrap.messages.MessagesMarkAsRead
import vk.scout.wrap.users.UsersGet
import vk.scout.main.display.{ShowMessage, MessageToDisplay}

class MessageReceiverActor(val msgDsplActor: Actor) extends Actor {

  object Check

  val displayedMsgsSet = collection.mutable.Set[Int]()

  def act() {
    def sendToDisplay(msgVk: MessageFromVk) {
      if (displayedMsgsSet.contains(msgVk.id)) return
      displayedMsgsSet += msgVk.id
      val userGet = UsersGet(Set(msgVk.userId))
      val asRead = MessagesMarkAsRead(Set(msgVk.id), msgVk.userId)
      val asImportant = MessagesMarkAsImportant(Set(msgVk.id), important = true)
      msgDsplActor ! ShowMessage(MessageToDisplay(msgVk, userGet, asRead, asImportant))
    }

    sendToDisplay(MessageFromVk(0, 0, 1, "Авторизация прошла успешно", None))
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

