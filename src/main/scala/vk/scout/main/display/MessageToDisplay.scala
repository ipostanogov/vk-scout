package vk.scout.main.display

import vk.scout.wrap.messages.MessageFromVk
import vk.scout.wrap.users.UserFromVk
import vk.scout.wrap.messages.MessagesMarkAsRead
import vk.scout.wrap.messages.MessagesMarkAsImportant
import vk.scout.wrap.users.UsersGet
import vk.scout.helpers._

case class MessageToDisplay(msgVk: MessageFromVk, usersGetRequestForSender: UsersGet,
                            markRead: MessagesMarkAsRead, markImportant: MessagesMarkAsImportant) {
  val id = msgVk.id
  lazy val text: String = msgVk.bodyWithImg
  val URL: String = "https://vk.com/im?sel=" + msgVk.userId
  val authorId = msgVk.userId
  private[this] lazy val cut = listExtractor[UserFromVk](usersGetRequestForSender.send(), Seq("response"))
  lazy val author: String = cut.head.firstName + " " + cut.head.lastName

  def run(command: => Unit) {
    new Thread(new Runnable {
      def run() {
        command
      }
    }).start()
  }

  def markAsRead() {
    run {
      markRead.send()
    }
  }

  def markAsImportant() {
    run {
      markImportant.send()
    }
  }
}

case class ShowMessage(msg: MessageToDisplay)