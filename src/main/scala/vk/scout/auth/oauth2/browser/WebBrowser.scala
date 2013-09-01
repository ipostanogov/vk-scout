package vk.scout.auth.oauth2.browser

import javafx.application.Platform
import javafx.embed.swing.JFXPanel
import javafx.scene.Scene
import javafx.scene.web.{WebEngine, WebView}
import javax.swing._
import java.awt._
import java.net.{MalformedURLException, URL}
import java.awt.event.{WindowAdapter, WindowEvent}
import javafx.beans.value.{ObservableValue, ChangeListener}

abstract class WebBrowser extends JQueryWebView {
  // inspired by http://docs.oracle.com/javafx/2/swing/SimpleSwingBrowser.java.htm
  protected[this] val frame: JFrame = new JFrame
  private[this] val jfxPanel: JFXPanel = new JFXPanel
  protected[this] var engine: WebEngine = null
  private[this] val panel: JPanel = new JPanel(new BorderLayout)

  // https://forums.oracle.com/thread/2395986
  Platform.setImplicitExit(false)

  frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE)
  frame.setPreferredSize(new Dimension(1024, 600))
  createScene()
  panel.add(jfxPanel, BorderLayout.CENTER)
  frame.getContentPane.add(panel)
  frame.pack()
//  frame.setVisible(true)


  protected[this] def modifyEngineListeners(): Unit

  private[this] def createScene() {
    Platform.runLater(new Runnable {
      def run() {
        val view: WebView = new WebView
        engine = view.getEngine

        engine.locationProperty.addListener(new ChangeListener[String] {
          def changed(ov: ObservableValue[_ <: String], oldValue: String, newValue: String) {
            SwingUtilities.invokeLater(new Runnable {
              def run() {
                frame.setTitle(newValue)
              }
            })
          }
        })

        modifyEngineListeners()
        jfxPanel.setScene(new Scene(view))
      }
    })
  }

  protected[this] def loadURL(url: String) {
    Platform.runLater(new Runnable {
      def run() {
        var tmp: String = toURL(url)
        if (tmp == null) {
          tmp = toURL("http://" + url)
        }
        engine.load(tmp)
      }
    })
  }

  private[this] def toURL(str: String): String = {
    try {
      new URL(str).toExternalForm
    }
    catch {
      case exception: MalformedURLException => {
        return null
      }
    }
  }

  protected[this] def close() = frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING))

  protected[this] def notifyOnClose(lock: AnyRef) {
    // inspired by http://stackoverflow.com/questions/1341699/how-do-i-make-a-thread-wait-for-jframe-to-close-in-java
    frame.addWindowListener(new WindowAdapter {
      override def windowClosing(arg0: WindowEvent) {
        lock synchronized {
          lock.notify()
        }
      }
    })
  }

}
