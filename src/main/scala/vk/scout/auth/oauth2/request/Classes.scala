package vk.scout.auth.oauth2.request

sealed trait EnumWithStringValue { val value: String }

sealed trait ResponseType extends EnumWithStringValue
object Token extends ResponseType { val value = "token" }
object Code extends ResponseType { val value = "code" }

sealed trait Scope extends EnumWithStringValue
object Friends extends Scope {val value = "friends"}
object Messages extends Scope {val value = "messages"}
object Status extends Scope {val value = "status"}

sealed trait DisplayType extends EnumWithStringValue
object Page extends DisplayType {val value = "page"}
object Popup extends DisplayType {val value = "popup"}