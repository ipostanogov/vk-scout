package vk.scout

import scala.annotation.tailrec
import scala.actors.Actor
import vk.scout.config.Config
import vk.scout.helpers.{ProcessUtils, MagicHeapCleaner, Timer}
import vk.scout.config.LoginPassword
import vk.scout.auth.oauth2.browser.AuthBrowser
import vk.scout.auth.oauth2.response.{AuthResponse, SuccessAuthData}
import vk.scout.auth.oauth2.request.RequestData
import vk.scout.auth.LoginDialog
import vk.scout.main.{Status, TrayKeeper}
import vk.scout.main.receive.MessageReceiverActor
import vk.scout.main.display.MessageDisplayActor
import vk.scout.wrap.InvalidAccessToken

object Launcher extends App with Actor {
  val URL = new RequestData("3819747").getUrl
  ProcessUtils.RestartIfHeapSizeNeedsReducing()
  MagicHeapCleaner.run()

  TrayKeeper.start()
  TrayKeeper ! Status("авторизация")
  val accessToken = auth(Config.getLoginPassword) match {
    case Some(successAuthData: SuccessAuthData) => Some(successAuthData.accessToken)
    case _ => None
  }

  @tailrec
  private def auth(optLoginPassword: Option[LoginPassword]): Option[AuthResponse] = {
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

  start()

  def act() {
    accessToken match {
      case Some(_) =>
        val msgDsplActor = new MessageDisplayActor
        val msgRcvrActor = new MessageReceiverActor(msgDsplActor)
        msgDsplActor.start()
        msgRcvrActor.start()
        TrayKeeper ! Status("подключён")
      case None =>
        this ! InvalidAccessToken
    }
    loop {
      react {
        case InvalidAccessToken =>
          Timer(5000) {
            ProcessUtils.tryRestartProgramOrExit(0xACCC0DE)
          }
          exit()
      }
    }
  }
}


