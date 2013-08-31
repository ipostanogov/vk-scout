package vk.scout.auth

import swing._
import scala.swing.BorderPanel.Position._
import scala.swing.event.{WindowClosing, ButtonClicked}
import vk.scout.config.LoginPassword

object LoginDialog extends Dialog {
  // http://stackoverflow.com/questions/7921182/scala-swing-newbie
  // http://alvinalexander.com/java/jwarehouse/scala/src/swing/scala/swing/test/UIDemo.scala.shtml
  var optLoginPassword: Option[LoginPassword] = None
  val loginField = new TextField(25)
  val loginLabel = new Label("Номер телефона / E-mail:")
  val passwordField = new PasswordField(25)
  val passwordLabel = new Label("Пароль:")
  val saveCheckbox = new CheckBox("Запомнить логин & пароль") {
    selected = true
    focusPainted = false
  }

  title = "Авторизация@vk.com"
  modal = true
  resizable = false

  val mainControls = new BoxPanel(Orientation.Vertical) {
    border = Swing.EmptyBorder(5, 5, 5, 5)
    contents += loginLabel
    contents += loginField
    contents += passwordLabel
    contents += passwordField
  }

  contents = new BorderPanel {
    layout(mainControls) = Center
    layout(new FlowPanel(FlowPanel.Alignment.Right)(
      saveCheckbox,
      Button("Войти") {
        if (saveCheckbox.selected)
          optLoginPassword = Some(LoginPassword(login = loginField.text, password = passwordField.password.mkString))
        close()
      }
    )) = South
  }

  listenTo(saveCheckbox)
  reactions += {
    case ButtonClicked(`saveCheckbox`) =>
      mainControls.visible = saveCheckbox.selected
      val sign = if (saveCheckbox.selected) 1 else -1
      size = new Dimension(size.width + sign * 20, size.height + sign * mainControls.size.height)
    case _ : WindowClosing =>
      // When user closes with 'x', this event rises
      optLoginPassword = None
  }
  centerOnScreen()

  def auth() = {
    peer.setAlwaysOnTop(true)
    open()
    optLoginPassword
  }
}