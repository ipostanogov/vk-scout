package vk.scout

import scala.annotation.tailrec
import scala.actors.Actor
import java.io.File
import vk.scout.config.Config
import vk.scout.helpers.Timer
import vk.scout.config.LoginPassword
import vk.scout.auth.oauth2.browser.AuthBrowser
import vk.scout.auth.oauth2.response.{AuthResponse, SuccessAuthData}
import vk.scout.auth.oauth2.request.RequestData
import vk.scout.auth.LoginDialog
import vk.scout.main.{Status, TrayKeeper}
import vk.scout.main.receive.MessageReceiverActor
import vk.scout.main.display.MessageDisplayActor
import scala.collection.JavaConverters._

object Launcher extends App with Actor {

  object Restart

  TrayKeeper.start()
  TrayKeeper ! Status("авторизация")
  val URL = new RequestData("3819747").getUrl
  val accessToken = auth(Config.getLoginPassword) match {
    case Some(successAuthData: SuccessAuthData) => Some(successAuthData.accessToken)
    case _ => None
  }

  start()

  def act() {
    run()
    loop {
      react {
        case Restart =>
          Timer(5000) {
            restartApplication()
          }
          exit()
      }
    }
  }

  def run() {
    accessToken match {
      case Some(_) =>
        val msgDsplActor = new MessageDisplayActor
        val msgRcvrActor = new MessageReceiverActor(msgDsplActor)
        msgDsplActor.start()
        msgRcvrActor.start()
        TrayKeeper ! Status("подключён")
      case _ =>
        this ! Restart
    }
  }

  @tailrec
  def auth(optLoginPassword: Option[LoginPassword]): Option[AuthResponse] = {
    optLoginPassword match {
      case optLP@Some(_) =>
        new AuthBrowser().auth(optLP, displayForever = false) match {
          case authResult@Some(_) => authResult
          case None => auth(None)
        }
      case None =>
        LoginDialog.auth() match {
          case optLP@Some(_) =>
            Config.save(optLP.get)
            auth(optLP)
          case None =>
            new AuthBrowser().auth(displayForever = true)
        }
    }
  }

  def restartApplication() {
    // http://stackoverflow.com/a/4160543
    val javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java"
    val currentJar = new File(getClass.getProtectionDomain.getCodeSource.getLocation.toURI)
    if (!currentJar.getName.endsWith(".jar")) return
    val builder: ProcessBuilder = new ProcessBuilder(Seq(javaBin, "-jar", currentJar.getPath).asJava)
    builder.start
    System.exit(0)
  }
}


