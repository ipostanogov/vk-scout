package vk.spy

import vk.scout.wrap.users.UserFromVk
import scala.swing._
import scala.swing.event.ButtonClicked
import javax.swing.UIManager
import BorderPanel.Position._

object MessageStat extends SimpleSwingApplication {
  UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName)

  def top = new MainFrame {
    title = "Загрузчик сообщений"
    contents = ui
    size = new Dimension(300, 80)
    peer.setLocationRelativeTo(null)
  }

  lazy val ui = new BorderPanel {
    val btSubmit = new Button("Скачать сообщения")
    val idField = new TextField()
    var table = new Table()
    listenTo(btSubmit)
    reactions += {
      case ButtonClicked(`btSubmit`) =>
        showPostsOnUserWall(UserFromVk.fromId(idField.text))
    }
    layout(btSubmit) = South
    layout(idField) = Center
    layout(new Label(" id Пользователя ")) = West
  }

  def showPostsOnUserWall(user: UserFromVk) {
    new SimpleSwingApplication {
      def top = new MainFrame {
        title = "Сообщения. " + user.firstName + " " + user.lastName
        val headers = Seq("Имя", "Фамилия", "Дата", "Текст")
        val rowData = user.postsOnWall.map(post => Array(post.author.firstName, post.author.lastName, post.normDate.toString, post.text).asInstanceOf[Array[Any]]).toArray
        contents = new ScrollPane(new Table(rowData, headers))
      }
    }.main(Array())
  }
}


