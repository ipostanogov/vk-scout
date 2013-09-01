package vk.scout.main.display

import scala.actors.Actor
import java.awt.{Point, GraphicsEnvironment, Dimension}
import vk.scout.helpers._

class MessageDisplayActor extends Actor {
  private[this] val defaultSize = new Dimension(200, 170)
  private[this] val rect = GraphicsEnvironment.getLocalGraphicsEnvironment.getDefaultScreenDevice.getDefaultConfiguration.getBounds
  private[this] val xMax = rect.getMaxX - defaultSize.getWidth - 5
  private[this] val yMax = rect.getMaxY - 50
  private[this] val displayingMsgs = Array.fill(3)(None: Option[MessageWindow])
  private[this] val waitingQueue = new collection.mutable.SynchronizedQueue[MessageWindow]

  def act() {
    loop {
      react {
        case ShowMessage(msg) =>
          val msgFrame = new MessageWindow(msg, this)
          if (displayingMsgs.forall(_.isDefined))
            waitingQueue += msgFrame
          else
            displayMsgFrame(msgFrame)
        case OnMessageWindowHide(window) =>
          removeFromDisplaying(window)
      }
    }
  }

  private[this] def removeFromDisplaying(msgFrame: MessageWindow) {
    val itemWithIndex = displayingMsgs.zipWithIndex.filter(_._1 == Some(msgFrame))
    // If message has received multiple times. E.g. user clicked close when frame closes itself after timeout
    if (!itemWithIndex.isEmpty) {
      displayingMsgs(itemWithIndex.head._2) = None
      Timer(1000) {
        waitingQueue synchronized {
          if (!waitingQueue.isEmpty)
            displayMsgFrame(waitingQueue.dequeue())
        }
      }
    }
  }

  private[this] def displayMsgFrame(msgFrame: MessageWindow) {
    val emptyIndex = displayingMsgs.zipWithIndex.filter(!_._1.isDefined).map(_._2).min
    displayingMsgs(emptyIndex) = Option(msgFrame)
    val loc = new Point(xMax.toInt, (yMax - (emptyIndex + 1) * (defaultSize.getHeight + 3) + 10).toInt)
    msgFrame.show(loc, defaultSize)
  }
}



