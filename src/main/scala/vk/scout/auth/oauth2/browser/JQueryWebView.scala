package vk.scout.auth.oauth2.browser

import javafx.scene.web.WebEngine
import javax.xml.transform.{OutputKeys, TransformerFactory, Transformer}
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import java.io.OutputStreamWriter

trait JQueryWebView {
  // inspired by http://code.google.com/p/javafx-demos/source/browse/trunk/javafx-demos/src/main/java/com/ezest/javafx/demogallery/webview/JQueryWebView.java?r=25
  final val DEFAULT_JQUERY_MIN_VERSION = "2.0.3"
  final val JQUERY_LOCATION = s"http://yandex.st/jquery/$DEFAULT_JQUERY_MIN_VERSION/jquery.min.js"
  /**
   * Executes a script which may reference jQuery function on a document.
   * Checks if the document loaded in a webEngine has a version of jQuery corresponding to
   * the minimum required version loaded, and, if not, then loads jQuery into the document
   * from the default JQUERY_LOCATION.
   * @param engine the webView engine to be used.
   * @param jQueryLocation the location of the jQuery script to be executed.
   * @param minVersion the minimum version of jQuery which needs to be included in the document.
   * @param script provided javascript script string (which may include use of jQuery functions on the document).
   * @return the result of the script execution.
   */
  def executejQuery(engine: WebEngine, minVersion: String, jQueryLocation: String, script: String) = {
    engine.executeScript("(function(window, document, version, callback) { " + "var j, d;" + "var loaded = false;" + "if (!(j = window.jQuery) || version > j.fn.jquery || callback(j, loaded)) {" + " var script = document.createElement(\"script\");" + " script.type = \"text/javascript\";" + " script.src = \"" + jQueryLocation + "\";" + " script.onload = script.onreadystatechange = function() {" + " if (!loaded && (!(d = this.readyState) || d == \"loaded\" || d == \"complete\")) {" + " callback((j = window.jQuery).noConflict(1), loaded = true);" + " j(script).remove();" + " }" + " };" + " document.documentElement.childNodes[0].appendChild(script) " + "} " + "})(window, document, \"" + minVersion + "\", function($, jquery_loaded) {" + script + "});")
  }

  def executejQuery(engine: WebEngine, minVersion: String, script: String): AnyRef = {
    executejQuery(engine, DEFAULT_JQUERY_MIN_VERSION, JQUERY_LOCATION, script)
  }

  def executejQuery(engine: WebEngine, script: String): AnyRef = {
    executejQuery(engine, DEFAULT_JQUERY_MIN_VERSION, script)
  }

  def printDocument(document: org.w3c.dom.Document) {
    try {
      val transformer: Transformer = TransformerFactory.newInstance.newTransformer
      transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no")
      transformer.setOutputProperty(OutputKeys.METHOD, "xml")
      transformer.setOutputProperty(OutputKeys.INDENT, "yes")
      transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8")
      transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4")
      transformer.transform(new DOMSource(document), new StreamResult(new OutputStreamWriter(System.out, "UTF-8")))
    }
    catch {
      case ex: Exception => {
        ex.printStackTrace()
      }
    }
  }

}
