package vk.scout.auth.rsa

import java.security.interfaces.{RSAPublicKey, RSAPrivateKey}
import java.security.spec.{X509EncodedKeySpec, PKCS8EncodedKeySpec}
import java.security.KeyFactory
import javax.crypto.Cipher

// http://stackoverflow.com/questions/3441501/java-asymmetric-encryption-preferred-way-to-store-public-private-keys

trait RSAContainer {
  protected[this] val cipher = Cipher.getInstance("RSA")
  protected[this] val keyFactory = KeyFactory.getInstance(cipher.getAlgorithm)
}

trait RSAEncryptor extends RSAContainer {
  val publicKeyBytes: Array[Byte]

  protected[this] def getPublicKey: RSAPublicKey = {
    val publicKeySpec: X509EncodedKeySpec = new X509EncodedKeySpec(publicKeyBytes)
    keyFactory.generatePublic(publicKeySpec).asInstanceOf[RSAPublicKey]
  }

  def encrypt(input: String) = {
    cipher.init(Cipher.ENCRYPT_MODE, getPublicKey)
    cipher.doFinal(input.getBytes)
  }
}

trait RSADecryptor extends RSAContainer {
  val privateKeyBytes: Array[Byte]

  protected[this] def getPrivateKey: RSAPrivateKey = {
    val spec: PKCS8EncodedKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes)
    keyFactory.generatePrivate(spec).asInstanceOf[RSAPrivateKey]
  }

  def decrypt(input: Array[Byte]) = {
    cipher.init(Cipher.DECRYPT_MODE, getPrivateKey)
    new String(cipher.doFinal(input))
  }
}