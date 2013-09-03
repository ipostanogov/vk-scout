package vk.scout.main.display

import scala.actors.Actor
import java.awt.{Point, GraphicsEnvironment, Dimension}
import vk.scout.helpers._
import vk.scout.main._
import vk.scout.main.SetMouseLeftClickOnTrayReceiver

class MessageDisplayActor extends Actor {
  private[this] val defaultSize = new Dimension(200, 170)
  private[this] val (xMax, yMax) = {
    val rect = GraphicsEnvironment.getLocalGraphicsEnvironment.getDefaultScreenDevice.getDefaultConfiguration.getBounds
    (rect.getMaxX - defaultSize.getWidth - 5, rect.getMaxY - 50)
  }
  private[this] val displaying = Array.fill(3)(None: Option[MessageWindow])
  private[this] val unconfirmed = new collection.mutable.SynchronizedQueue[MessageWindow]
  private[this] var idsOfTwiceDisplayed = Set(0)
  private[this] val waiting = new collection.mutable.SynchronizedQueue[MessageWindow]

  def act() {
    TrayKeeper ! SetMouseLeftClickOnTrayReceiver(this)
    loop {
      react {
        case ShowMessage(msg) =>
          val msgFrame = new MessageWindow(msg, this)
          if (displaying.forall(_.isDefined))
            waiting += msgFrame
          else
            displayMsgFrame(msgFrame)
        case ClosedByUser(window) =>
          removeFromDisplaying(window)
        case ClosedByTimeout(window) =>
          removeFromDisplaying(window)
          if (!idsOfTwiceDisplayed.contains(window.message.id)) {
            unconfirmed.enqueue(window)
            TrayKeeper ! SetMsgIcon
          }
        case LeftMouseClickOnTrayIcon =>
          TrayKeeper ! SetOwlIcon
          unconfirmed.dequeueAll(_ => true).foreach(w => {
            idsOfTwiceDisplayed += w.message.id
            this ! ShowMessage(w.message)
          })
      }
    }
  }

  private[this] def removeFromDisplaying(msgFrame: MessageWindow) {
    val itemWithIndex = displaying.zipWithIndex.filter(_._1 == Some(msgFrame))
    // If message has received multiple times. E.g. user clicked close when frame closes itself after timeout
    if (!itemWithIndex.isEmpty) {
      displaying(itemWithIndex.head._2) = None
      Timer(1000) {
        waiting synchronized {
          if (!waiting.isEmpty)
            displayMsgFrame(waiting.dequeue())
        }
      }
    }
  }

  private[this] def displayMsgFrame(msgFrame: MessageWindow) {
    val emptyIndex = displaying.zipWithIndex.filter(!_._1.isDefined).map(_._2).min
    displaying(emptyIndex) = Option(msgFrame)
    val loc = new Point(xMax.toInt, (yMax - (emptyIndex + 1) * (defaultSize.getHeight + 3) + 10).toInt)
    msgFrame.show(loc, defaultSize)
  }
}



