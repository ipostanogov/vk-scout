package vk.scout.main.display

import vk.scout.wrap.messages.MessageFromVk
import vk.scout.wrap.users.UserFromVk
import vk.scout.wrap.messages.MessagesMarkAsRead
import vk.scout.wrap.messages.MessagesMarkAsImportant
import vk.scout.wrap.users.UsersGet
import vk.scout.helpers._

case class MessageToDisplay(msgVk: MessageFromVk) {
  lazy val id = msgVk.id
  lazy val text: String = msgVk.bodyWithImg
  lazy val URL: String = "https://vk.com/im?sel=" + (msgVk.chatId match {
    case Some(chat_id) => "c" + chat_id
    case None => msgVk.userId.toString
  })
  lazy val authorId = msgVk.userId
  private[this] val usersGet = UsersGet(Set(msgVk.userId))
  lazy val author: String = listExtractor[UserFromVk](usersGet.send(), Seq("response")) match {
    case List(user) => user.firstName + " " + user.lastName + (msgVk.chatId match {
      case Some(_) =>  " @ " + msgVk.title
      case None => ""
    })
    case _ => throw new IllegalArgumentException(usersGet.toString)
  }

  private[this] def run(command: => Unit) {
    new Thread(new Runnable {
      def run() {
        command
      }
    }).start()
  }

  def markAsRead() {
    run {
      MessagesMarkAsRead(Set(msgVk.id), msgVk.userId).send()
    }
  }

  def markAsImportant() {
    run {
      MessagesMarkAsImportant(Set(msgVk.id), important = true).send()
    }
  }
}

case class ShowMessage(msg: MessageToDisplay)