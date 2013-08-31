package vk.scout.auth.oauth2.request

sealed trait EnumWithStringValue { val value: String }

sealed trait ResponseType extends EnumWithStringValue
case object Token extends ResponseType { val value = "token" }
case object Code extends ResponseType { val value = "code" }

sealed trait Scope extends EnumWithStringValue
case object Friends extends Scope {val value = "friends"}
case object Messages extends Scope {val value = "messages"}
case object Status extends Scope {val value = "status"}

sealed trait DisplayType extends EnumWithStringValue
case object Page extends DisplayType {val value = "page"}
case object Popup extends DisplayType {val value = "popup"}