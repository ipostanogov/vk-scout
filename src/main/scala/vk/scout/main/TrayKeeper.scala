package vk.scout.main

import scala.actors.Actor
import java.awt._
import javax.imageio.ImageIO
import java.io.ByteArrayInputStream
import org.apache.commons.codec.binary.Base64
import java.awt.event._
import java.awt.TrayIcon.MessageType
import javax.swing.SwingUtilities

object TrayKeeper extends Actor {
  val appName = "VK Scout"
  var lmcReceiver : Option[Actor] = None
  // base64 of http://ru.iconka.com/harry-potter-and-windows-xp/ `owl16.png`
  val owlIcon = base64ToImage("iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAACp0lEQVR42pxS30tTYRh+zs7Z3JnTtVlmzp9bpoGGaAVFBApBBRUESRfRRRd13U1QIETQHxB1U2BBFMgIIu8k0KI0luamK63NqdP2w8Em++HOzs+v74wCFfWi5+I77/ty3ud73+f5AAqPx8PiP0AIAfPIM9mgMexjd03lYLvLfp01GHoVTUNRVEbmItmnK6n8JWjas9t9XV+2JdCD4YnFq5JMXhVEla2vLoeJNSAUy4JlGBVQb/X1tA3sNEEJ0+GEby0rEB0/FlPk688EUVSNJFI58skf8e22Qgn+YFyUZIVcOAIi06/8N9ZrH6eWxN0IOD0QJXV2anapc2ia4PWTe5DFHPTYOxOGojCh3YQ06IcgSHdSWVF5+W4MoqJBkjW8ePsZiXQB1nK+dTIYf0BvY3ckkBm2u4y3ksOHmomjrgOOxi50tLnQ6nJin93C1VVV9gfCydXB94HL244xPBGJj/pWSDyVJ6qmEZUKuJYrkqVEhqSpuLmCVMrHZpbnnw99692oQWkC6qVVTxyVZloFNBqzBoauooJhGFh5IzhqrdtZ5bbwZfc3PrwSAWV4Qy+l74UKSpuKkgo9p+IiFEkiL8igttKahrpqW3WOd1/cRGDUtLvUuuRCNIWCqKBQlLFOmwRJwc/FGL6HoqWaTm6zmltNHHdlE0HP8eaEAey5cV9Ipjsjsy4inSsikxfBGssw6g3A/2sFAiWX6SQmI9e8eQWKsyeapjgjd9obWEgur+awvJpBTpBgNPGwlFfA65vD6Lg/SUkk3mxs+NfHbHXkw8P2Ae/vipZjRzu7R7zBYlvTXkdgIQ215Rr22Gw4ULN/vr62JiqL2s3zp1xBbisBS4Qb2WgYRmcSJBGzNDbUYi4ZQ+OZftgddvBm80Gqk5O6dpL+HvwjwAC5ZYFoTw9ZfgAAAABJRU5ErkJggg==")
  // base64 of http://www.visualpharm.com/must_have_icon_set/ `Mail_16x16.png`
  val mailIcon = base64ToImage("iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAABvUlEQVQ4jcWTzWpTURDHf+eec89Nzm1qgrHXBD8W0S4qCFZB6EJXPoNbH8SnEFe+gm9gIaKgULCbmJJSav2AJo2JuZqkufm4ucfFJcaNm2wcmMXMMH/+858ZYa1lFRNCAOCs1P030LNqZzUKwLDfRwE82TaczgxryYhYeigHfDlnYDUXGBFJgzufAjASmoABkZfj6Yt6OsKbkwG+jFHGIKTEUZKp1BgXYm3QEoTWOFqzYSBSGV5/bC01qJTzVOtnhMMxvgYLJDZ1a9NYCMi4cPYz4uVei0q5sARYNx53bgTsHf+g/i0kq9LCotkBXAf2T0KqjR7bmyXyax5AqgGAkg5BIcfhaY/2rzEPtwJc6SAExEnCbq1NbzjhykYBJZfLUwCzeE7tc4esK3i8c43uYMpurcm9ShEBfPjU5f7NIsWc5m2jw8GXiLuVi8s1lvwZt66uc73oEycQJ3A+jnl/1AFgZ/MSfkahHFAOfO2ec9wa8updI2Xw6HaAtYL+2LI4CqUkD7Yu/6E6iS0TQAClvKFcyC4B2q2QMHYZCY9ZIjBugj8ZMJXeP3OuSnVQ35tNnh+ErPwT//+ZVmWwsN8umK/0JoimbwAAAABJRU5ErkJggg==")

  def act() {
    makeIconInTray() match {
      case Some(trayIcon) =>
        loop {
          react {
            case Status(text) => trayIcon.setToolTip(s"$appName ~ $text")
            case n: Notify => trayIcon.displayMessage(n.caption, n.text, n.msgType)
            case SetMouseLeftClickOnTrayReceiver(receiver) => lmcReceiver = Some(receiver)
            case setIcon : SetIcon => trayIcon.setImage(setIcon match {
              case SetOwlIcon => owlIcon
              case SetMsgIcon => mailIcon
            })
          }
        }
      case None =>
    }
  }

  def makeIconInTray() = {
    // http://www.oracle.com/technetwork/articles/javase/systemtray-139788.html
    // http://stackoverflow.com/questions/15477152/how-do-i-convert-a-image-as-a-string-to-png-file (Approach 2)
    if (SystemTray.isSupported) {
      val popup: PopupMenu = new PopupMenu {
        val defaultItem: MenuItem = new MenuItem("Выход") {
          addActionListener(new ActionListener {
            def actionPerformed(e: ActionEvent) {
              System.exit(0)
            }
          })
        }
        add(defaultItem)
      }
      val trayIcon = new TrayIcon(owlIcon, appName, popup) {
        setImageAutoSize(true)
        addMouseListener(new MouseAdapter {
          override def mouseClicked(e: MouseEvent) {
            if (SwingUtilities.isLeftMouseButton(e))
              lmcReceiver.map(_ ! LeftMouseClickOnTrayIcon)
          }
        })
      }
      try {
        SystemTray.getSystemTray.add(trayIcon)
      }
      catch {
        case e: AWTException => {
          System.err.println("TrayIcon could not be added.")
        }
      }
      Some(trayIcon)
    }
    else None

  }

  def base64ToImage(base64 : String) = ImageIO.read(new ByteArrayInputStream(Base64.decodeBase64(base64.getBytes)))
}

case class SetMouseLeftClickOnTrayReceiver(receiver: Actor)
object LeftMouseClickOnTrayIcon

sealed trait SetIcon
object SetOwlIcon extends SetIcon
object SetMsgIcon extends SetIcon

case class Status(text: String)
case class Notify(caption: String, text: String, msgType: MessageType)
object FileError extends Notify(caption = "Ошибка", text = "Файл с настройками повреждён", msgType = MessageType.WARNING)