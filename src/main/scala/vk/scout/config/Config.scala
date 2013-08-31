package vk.scout.config

import java.io.{PrintWriter, File}
import argonaut._, Argonaut._
import vk.scout.helpers._
import javax.crypto.{BadPaddingException, IllegalBlockSizeException}
import vk.scout.auth.rsa.{RSAToolFromFile, RSATool}
import vk.scout.main.{FileError, TrayKeeper}
import java.awt.Desktop
import java.net.URL
import scala.collection.JavaConverters._

object Config {
  val fileName = System.getProperty("user.home") + File.separator + ".vk-scout"
  val file = new File(fileName)
  val RSAKeysFileNamesStr = "RSAKeysFileNames"
  val EncLoginPasswordStr = "EncLoginPassword"
  val WebBrowserStr = "WebBrowser"
  var _rsaKeysFileNames: Option[RSAKeysFileNames] = None

  def rsaKeysFileNames = _rsaKeysFileNames

  def rsaKeysFileNames_=(optFileNames: Option[RSAKeysFileNames]) {
    _rsaKeysFileNames = optFileNames match {
      case Some(fileNames) =>
        if (new File(fileNames.privateKeyFileName).exists() && new File(fileNames.publicKeyFileName).exists())
          optFileNames
        else
          None
      case _ => None
    }
  }

  def RSATool = {
    rsaKeysFileNames match {
      case Some(fileNames) => rsaKeysFileNames = Some(fileNames)
        new RSAToolFromFile {
          protected[this] val publicKeyFileName = fileNames.publicKeyFileName
          protected[this] val privateKeyFileName = fileNames.privateKeyFileName
        }
      case _ =>
        new RSATool {
          val publicKeyBytes: Array[Byte] = "48,-127,-97,48,13,6,9,42,-122,72,-122,-9,13,1,1,1,5,0,3,-127,-115,0,48,-127,-119,2,-127,-127,0,-77,-41,-36,-16,122,-37,-111,-32,108,-97,-5,-18,65,100,-105,-99,65,-112,-80,-117,48,-119,-9,-79,85,-102,-59,-26,-61,81,90,-66,-105,0,-7,125,69,-116,73,97,-106,94,75,77,-41,-57,-97,-102,-24,-108,1,83,-76,-83,-99,92,-102,115,-59,-117,-76,83,42,-11,35,-6,-33,-69,-67,40,-106,127,-114,-8,13,-87,51,58,-53,10,-86,-74,86,-57,51,39,45,0,121,59,105,35,11,1,124,33,81,26,36,-62,7,-24,-24,126,-87,95,-119,-12,-99,59,17,31,-73,-48,20,-22,47,-113,70,-128,-10,-123,-18,31,-9,-103,14,-55,2,3,1,0,1".split(",").map(_.toByte)
          val privateKeyBytes: Array[Byte] = "48,-126,2,119,2,1,0,48,13,6,9,42,-122,72,-122,-9,13,1,1,1,5,0,4,-126,2,97,48,-126,2,93,2,1,0,2,-127,-127,0,-77,-41,-36,-16,122,-37,-111,-32,108,-97,-5,-18,65,100,-105,-99,65,-112,-80,-117,48,-119,-9,-79,85,-102,-59,-26,-61,81,90,-66,-105,0,-7,125,69,-116,73,97,-106,94,75,77,-41,-57,-97,-102,-24,-108,1,83,-76,-83,-99,92,-102,115,-59,-117,-76,83,42,-11,35,-6,-33,-69,-67,40,-106,127,-114,-8,13,-87,51,58,-53,10,-86,-74,86,-57,51,39,45,0,121,59,105,35,11,1,124,33,81,26,36,-62,7,-24,-24,126,-87,95,-119,-12,-99,59,17,31,-73,-48,20,-22,47,-113,70,-128,-10,-123,-18,31,-9,-103,14,-55,2,3,1,0,1,2,-127,-128,113,22,25,-51,-11,-45,63,-59,121,72,-104,104,87,34,99,-37,-12,-80,-128,-14,-27,80,-68,57,-4,116,88,97,37,12,113,29,75,98,-57,127,40,78,112,-114,54,-71,-112,108,-96,116,-36,61,-46,101,-50,-9,-51,-77,38,50,102,-60,-55,-63,113,96,-94,-81,118,30,108,-83,81,99,-22,-21,-95,-102,103,64,-65,-127,-45,-14,-62,31,56,-68,122,82,98,47,13,52,-53,112,109,-25,-121,-99,70,-127,97,-14,-90,-113,90,-52,97,-1,-47,-68,-96,92,-57,21,13,53,84,-65,20,111,125,58,89,-122,-8,121,96,96,102,-119,2,65,0,-6,111,121,-65,40,-60,-5,82,-115,-10,-125,127,-63,-84,29,33,25,62,-108,106,101,-76,-126,40,-125,67,2,-64,-114,-93,-24,118,56,56,118,117,-41,-97,39,-52,102,-33,-91,-23,-58,-102,126,-82,39,-115,-114,0,-31,119,17,73,27,-45,-35,45,50,102,69,63,2,65,0,-73,-42,-40,109,96,-43,98,127,-31,-34,-18,-35,115,-32,-120,124,-75,12,-2,54,43,81,-48,-111,31,59,95,-25,116,13,-44,62,-126,91,-47,-18,15,90,1,-20,-62,-99,-56,-12,115,13,28,-17,-41,73,85,-15,-11,21,-109,120,11,-106,-62,3,-14,-103,1,-9,2,65,0,-74,22,-123,-100,3,37,-82,98,-87,11,-37,50,-15,-5,-107,-74,114,39,121,60,31,52,-17,10,75,-34,86,74,-15,-85,-91,-93,7,95,-82,106,34,91,-5,80,-8,95,-106,-65,-14,-126,116,-89,101,-28,-37,-94,50,77,35,-119,87,-83,100,-4,-82,-49,-125,123,2,64,104,-66,68,121,-83,94,102,-52,-82,8,-78,-40,2,25,-64,42,45,-115,-59,-103,95,125,49,-12,61,115,58,-27,72,-66,72,91,-123,50,-125,-99,-69,-87,112,-57,80,-21,77,127,-39,-67,114,-21,68,34,72,-111,-97,46,55,-29,-127,-42,-41,-100,32,49,5,105,2,65,0,-25,-15,-108,49,-100,32,87,52,-77,20,26,71,-107,117,30,-16,-76,-47,-50,92,-67,-58,-57,-55,106,-64,-93,98,38,12,-128,-82,-107,26,-108,-81,4,-58,115,-39,2,-121,-77,-86,81,43,-32,-60,-72,-119,-27,96,-32,114,8,10,70,-108,-14,0,42,-25,-81,-10".split(",").map(_.toByte)
        }
    }
  }

  def configFileToString(): String = {
    if (!file.exists) return ""
    try {
      val source = scala.io.Source.fromFile(fileName)
      val lines = source.getLines().mkString
      source.close()
      lines
    } catch {
      // RuntimeException on bad content (symbols, etc) in json
      // BadPaddingException & IllegalBlockSizeException if decode fails
      // And any other exception just to forget about this bad file
      case (_: RuntimeException | _: BadPaddingException | _: IllegalBlockSizeException | _: Throwable) =>
        TrayKeeper ! FileError
        ""
    }
  }

  def getLoginPassword: Option[LoginPassword] = {
    val conf = configFileToString()
    rsaKeysFileNames = fieldExtractor[RSAKeysFileNames](conf, Seq(RSAKeysFileNamesStr))
    fieldExtractor[EncLoginPasswordInString](conf, Seq(EncLoginPasswordStr)) match {
      case Some(encLoginPasswordInString) =>
        Some(encLoginPasswordInString.withBytes.decode(RSATool))
      case _ => None
    }
  }

  def save(loginPassword: LoginPassword) {
    try {
      val json = (EncLoginPasswordStr := loginPassword.encode(RSATool).withString) ->:
        (WebBrowserStr :=? webBrowser) ->?: (RSAKeysFileNamesStr :=? rsaKeysFileNames) ->?: jEmptyObject
      Some(new PrintWriter(fileName)).foreach {
        p => p.write(json.spaces2); p.close()
      }
    }
    catch {
      case _: Throwable => TrayKeeper ! FileError
    }
  }

  def webBrowser = fieldExtractor[List[String]](configFileToString(), Seq(WebBrowserStr))

  def openInWebBrowser(url: String) {
    webBrowser match {
      case Some(lst) => new ProcessBuilder((lst :+ url).asJava).start()
      case _ => Desktop.getDesktop.browse(new URL(url).toURI)
    }
  }
}


