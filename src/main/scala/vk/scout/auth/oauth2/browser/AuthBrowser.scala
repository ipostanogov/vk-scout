package vk.scout.auth.oauth2.browser

import javafx.beans.value.{ObservableValue, ChangeListener}
import org.w3c.dom.Document
import org.apache.http.client.utils.URLEncodedUtils
import scala.collection.JavaConverters._
import java.net.URI
import argonaut._, Argonaut._
import vk.scout.helpers.Timer
import vk.scout.config.LoginPassword
import vk.scout.auth.oauth2.response.{FailAuthData, SuccessAuthData, AuthResponse}
import vk.scout.Launcher
import javafx.application.Platform

class AuthBrowser extends WebBrowser {
  m =>
  var authResult: Option[AuthResponse] = None
  private val lock: AnyRef = new AnyRef
  notifyOnClose(lock)
  var loginPassword: Option[LoginPassword] = None
  var displayForever = false

  def auth(loginPassword: Option[LoginPassword] = None, displayForever: Boolean) = {
    m.loginPassword = loginPassword
    m.displayForever = displayForever
    frame.setVisible(true)
    modifyEngineListeners()
    reloadEngine()
    lock synchronized {
      lock.wait()
    }
    authResult
  }

  def reloadEngine() {
    Platform.runLater(new Runnable {
      def run() {
        if (engine != null && (engine.getDocument == null || engine.getDocument.getDocumentURI == "about:blank"))
          loadURL(Launcher.URL)
      }
    })
    Timer(if (engine != null) 15000 else 1000) {
      reloadEngine()
    }
  }

  val pasteLoginPassword: ChangeListener[Document] = new ChangeListener[Document] {
    def changed(prop: ObservableValue[_ <: Document], oldDoc: Document, newDoc: Document) {
      if (newDoc == null) return
      loginPassword.map(lp =>
      executejQuery(engine,
        "(function(){" +
          "$('input[name=email]').val('" + lp.login + "');" +
          "$('input[name=pass]').val('" + lp.password + "');" +
          "$('input[type=submit]').click();" +
          "})();"))
    }
  }

  val retrieveAccessToken: ChangeListener[Document] = new ChangeListener[Document] {
    def jsonFromUrl(doc: Document) = {
      // vk returns link like http://REDIRECT_URI#access_token= 533..6506a3&expires_in=86400&user_id=8492
      // parsing it with URLEncodedUtils fails
      val docURI = doc.getDocumentURI.replace("#", "?").replace(" ", "+")
      val urlParams = URLEncodedUtils.parse(new URI(docURI), "UTF-8").asScala.map(x => (x.getName, x.getValue)).toMap
      // https://github.com/argonaut-io/argonaut/blob/master/src/test/scala/argonaut/JsonParserSpecification.scala
      (for ((key, value) <- urlParams) yield key := value).foldRight(jEmptyObject)(_ ->: _)
    }

    def changed(prop: ObservableValue[_ <: Document], oldDoc: Document, newDoc: Document) {
      if (newDoc == null || newDoc.getDocumentURI.contains("client_id")) return
      val json = jsonFromUrl(newDoc)
      json.jdecode[SuccessAuthData].toOption match {
        case successAuthData@Some(_) =>
          authResult = successAuthData
          engine.documentProperty().removeListener(retrieveAccessToken)
          close()
        case _ =>
          if (displayForever)
            frame.setVisible(true)
          else {
            authResult = json.jdecode[FailAuthData].toOption
            close()
          }
      }
    }
  }

  protected def modifyEngineListeners() {
    if (pasteLoginPassword == null || retrieveAccessToken == null || engine == null || loginPassword == null) return
    engine.documentProperty.removeListener(pasteLoginPassword)
    engine.documentProperty.addListener(pasteLoginPassword)
    engine.documentProperty.removeListener(retrieveAccessToken)
    engine.documentProperty.addListener(retrieveAccessToken)
  }
}
