package vk.scout.wrap.users

sealed trait NameCase {def value : String}
object Nominative extends NameCase { val value = "nom" }

