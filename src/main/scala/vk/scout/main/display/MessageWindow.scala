package vk.scout.main.display

import javax.swing._
import java.awt._
import java.awt.event.{MouseAdapter, MouseEvent}
import java.lang.Exception
import com.sun.awt.AWTUtilities
import scala.actors.Actor
import vk.scout.helpers.Timer
import vk.scout.config.Config

class MessageWindow(val message: MessageToDisplay, msgDsplActor: Actor) {
  me =>
  private[this] val panel = new JPanel(new BorderLayout()) {
    val titledBorder = BorderFactory.createTitledBorder(message.author)
    setBorder(titledBorder)
    val textField = new JEditorPane("text/html", message.text) {
      putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true)
      setFont(MessageWindow.font)
      setOpaque(false)
    }
    add(textField, BorderLayout.NORTH)
  }

  private[this] val window = new JWindow() {
    setAlwaysOnTop(true)
    setContentPane(panel)
  }

  def show(loc: Point, size: Dimension) = {
    window.setLocation(loc)
    window.setSize(size)
    window.setVisible(true)
    animate(500, 1000, 5000, 40)
  }

  def changeOpacity(secondsLeft: Int, step: Int, lifetime: Int, inc: Boolean, low: Float, high: Float) {
    if (secondsLeft > 0 && window.isVisible) {
      val opacity = inc match {
        case true => high - (secondsLeft.toFloat / lifetime.toFloat) * (high - low)
        case false => (secondsLeft.toFloat / lifetime.toFloat) * (high - low) + low
      }
      AWTUtilities.setWindowOpacity(window, opacity)
      Timer(step) {
        changeOpacity(secondsLeft - step, step, lifetime, inc, low, high)
      }
    }
  }

  def animate(appearTime: Int, showTime: Int, hideTime: Int, step: Int) {
    changeOpacity(appearTime, step, appearTime, inc = true, 0.0f, 1.0f)
    Timer(appearTime + showTime) {
      changeOpacity(hideTime, step, hideTime, inc = false, 0.8f, 1.0f)
    }
    Timer(appearTime + showTime + hideTime) {
      close()
    }
  }

  def close() {
    if (window.isVisible) {
      window.setVisible(false)
      msgDsplActor ! OnMessageWindowHide(me)
    }
  }

  window.addMouseListener(new MouseAdapter {
    override def mouseClicked(e: MouseEvent) {
      close()
      if (SwingUtilities.isLeftMouseButton(e)) try {
        Config.openInWebBrowser(message.URL)
      }
      catch {
        case e: Exception => println(e)
      }
      else if (SwingUtilities.isRightMouseButton(e)) message.markAsRead()
      // Not working
      //      else if (SwingUtilities.isMiddleMouseButton(e)) message.markAsImportant()
    }
  })
}

object MessageWindow {
  val font = new JTextArea().getFont
}

case class OnMessageWindowHide(window: MessageWindow)