package vk.scout.auth.rsa

import java.io.{FileInputStream, File}

trait RSATool extends RSAEncryptor with RSADecryptor

trait RSAToolFromFile extends RSATool with BytesReader {
  protected[this] val publicKeyFileName: String
  protected[this] val privateKeyFileName: String
  protected[this] lazy val publicKeyBytes: Array[Byte] = readFromFile(publicKeyFileName)
  protected[this] lazy val privateKeyBytes: Array[Byte] = readFromFile(privateKeyFileName)
}

trait BytesReader {
  protected[this] def readFromFile(fileName: String) = {
    val file = new File(fileName)
    assert(file.exists())
    val keyBytes = new Array[Byte](file.length.toInt)
    val fis = new FileInputStream(file)
    fis.read(keyBytes)
    fis.close()
    keyBytes
  }
}