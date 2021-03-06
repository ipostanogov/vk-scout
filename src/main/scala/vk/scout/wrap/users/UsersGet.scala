package vk.scout.wrap.users

import vk.scout.wrap.ApiConnector

case class UsersGet(userIntIds: Set[Int] = Set(),
                    userScreenNames: Set[String] = Set(),
                    fields: Set[UserField] = Set(),
                    nameCase: NameCase = Nominative) extends ApiConnector {
  protected[this] final val methodName = "users.get"

  private[this] def userIds = userScreenNames ++ userIntIds.map(_.toString)

  protected[this] def apiParamsMap: Map[String, Option[String]] = Map(
    "user_ids" -> Option(userIds.mkString(",")),
    "fields" -> Option(fields.map(_.value).mkString(",")),
    "name_case" -> Option(nameCase.value)
  )
}