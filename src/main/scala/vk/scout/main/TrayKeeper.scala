package vk.scout.main

import scala.actors.Actor
import java.awt._
import javax.imageio.ImageIO
import java.io.ByteArrayInputStream
import org.apache.commons.codec.binary.Base64
import java.awt.event.{ActionEvent, ActionListener}
import java.awt.TrayIcon.MessageType

object TrayKeeper extends Actor {
  val appName = "VK Scout"

  def act() {
    makeIconInTray() match {
      case Some(trayIcon) =>
        loop {
          react {
            case Status(text) => trayIcon.setToolTip(s"$appName ~ $text")
            case n : Notify => trayIcon.displayMessage(n.caption, n.text, n.msgType)
          }
        }
      case None =>
    }
  }

  def makeIconInTray() = {
    // http://www.oracle.com/technetwork/articles/javase/systemtray-139788.html
    // http://stackoverflow.com/questions/15477152/how-do-i-convert-a-image-as-a-string-to-png-file (Approach 2)
    if (SystemTray.isSupported) {
      // base64 of http://ru.iconka.com/harry-potter-and-windows-xp/ `owl16.png`
      val imageData: String = "iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAACp0lEQVR42pxS30tTYRh+zs7Z3JnTtVlmzp9bpoGGaAVFBApBBRUESRfRRRd13U1QIETQHxB1U2BBFMgIIu8k0KI0luamK63NqdP2w8Em++HOzs+v74wCFfWi5+I77/ty3ud73+f5AAqPx8PiP0AIAfPIM9mgMexjd03lYLvLfp01GHoVTUNRVEbmItmnK6n8JWjas9t9XV+2JdCD4YnFq5JMXhVEla2vLoeJNSAUy4JlGBVQb/X1tA3sNEEJ0+GEby0rEB0/FlPk688EUVSNJFI58skf8e22Qgn+YFyUZIVcOAIi06/8N9ZrH6eWxN0IOD0QJXV2anapc2ia4PWTe5DFHPTYOxOGojCh3YQ06IcgSHdSWVF5+W4MoqJBkjW8ePsZiXQB1nK+dTIYf0BvY3ckkBm2u4y3ksOHmomjrgOOxi50tLnQ6nJin93C1VVV9gfCydXB94HL244xPBGJj/pWSDyVJ6qmEZUKuJYrkqVEhqSpuLmCVMrHZpbnnw99692oQWkC6qVVTxyVZloFNBqzBoauooJhGFh5IzhqrdtZ5bbwZfc3PrwSAWV4Qy+l74UKSpuKkgo9p+IiFEkiL8igttKahrpqW3WOd1/cRGDUtLvUuuRCNIWCqKBQlLFOmwRJwc/FGL6HoqWaTm6zmltNHHdlE0HP8eaEAey5cV9Ipjsjsy4inSsikxfBGssw6g3A/2sFAiWX6SQmI9e8eQWKsyeapjgjd9obWEgur+awvJpBTpBgNPGwlFfA65vD6Lg/SUkk3mxs+NfHbHXkw8P2Ae/vipZjRzu7R7zBYlvTXkdgIQ215Rr22Gw4ULN/vr62JiqL2s3zp1xBbisBS4Qb2WgYRmcSJBGzNDbUYi4ZQ+OZftgddvBm80Gqk5O6dpL+HvwjwAC5ZYFoTw9ZfgAAAABJRU5ErkJggg=="
      val image: Image = ImageIO.read(new ByteArrayInputStream(Base64.decodeBase64(imageData.getBytes)))

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
      val trayIcon = new TrayIcon(image, appName, popup) {
        setImageAutoSize(true)
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
}

case class Status(text: String)

case class Notify(caption: String, text: String, msgType: MessageType)

object FileError extends Notify(caption = "Ошибка", text = "Файл с настройками повреждён", msgType = MessageType.WARNING)