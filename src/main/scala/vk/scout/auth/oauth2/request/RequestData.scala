package vk.scout.auth.oauth2.request

import vk.scout.helpers.URLBuilder

class RequestData(val clientId: String) extends URLBuilder {
  protected[this] val pageUrl = "https://oauth.vk.com/authorize"
  protected[this] val redirectUrl = "http://oauth.vk.com/blank.html"
  protected[this] val responseType: ResponseType = Token
  protected[this] val scope: Traversable[Scope] = Traversable(Messages)
  protected[this] val displayType: DisplayType = Page

  protected[this] def allRequiredParamsMap = Map(
    "client_id" -> clientId,
    "redirect_uri" -> redirectUrl,
    "response_type" -> responseType.value,
    "scope" -> scope.map(_.value).mkString(","),
    "display" -> displayType.value
  )

  protected[this] def paramsMap = allRequiredParamsMap.map(x => (x._1, Option(x._2)))
}
