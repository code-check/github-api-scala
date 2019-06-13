package codecheck.github.api

import java.net.URLEncoder
import scala.concurrent.Promise
import scala.concurrent.Future
import org.json4s.jackson.JsonMethods
import org.json4s.DefaultFormats
import java.util.UUID
import codecheck.github.models.AccessToken
import codecheck.github.exceptions.OAuthAPIException
import codecheck.github.transport._

class OAuthAPI(clientId: String, clientSecret: String, redirectUri: String, client: Transport) {
  private implicit val format = DefaultFormats

  private val accessRequestUri = "https://github.com/login/oauth/authorize"
  private val tokenRequestUri = "https://github.com/login/oauth/access_token"

  def requestAccessUri(scope: String*) = {
    val params = Map[String, String](
      "client_id" -> clientId,
      "redirect_uri" -> redirectUri,
      "scope" -> scope.mkString(","),
      "response_type" -> "token",
      "state" -> UUID.randomUUID.toString
    )
    val query: String = params.map { case (k, v) => k +"="+ URLEncoder.encode(v, "utf-8") }.mkString("&")
    accessRequestUri +"?"+ query
  }

  def requestAccessUri(state: String, scope: Seq[String]) = {
    val params = Map[String, String](
      "client_id" -> clientId,
      "redirect_uri" -> redirectUri,
      "scope" -> scope.mkString(","),
      "response_type" -> "token",
      "state" -> state
    )
    val query: String = params.map { case (k, v) => k +"="+ URLEncoder.encode(v, "utf-8") }.mkString("&")
    accessRequestUri +"?"+ query
  }

  def requestToken(code: String): Future[AccessToken] = {
    val params: Map[String, String] = Map(
      "client_id" -> clientId,
      "client_secret" -> clientSecret,
      "code" -> code,
      "redirect_uri" -> redirectUri
    )
    val request = client.preparePost(tokenRequestUri)
      .setHeader("Content-Type", "application/x-www-form-urlencoded")
      .setHeader("Accept", "application/json")
      .setFollowRedirect(true)
    params.foreach { case (k, v) => request.addFormParam(k, v) }

    val deferred = Promise[AccessToken]()
    request.execute(new CompletionHandler() {
      def onCompleted(res: Response) = {
        val body = res.getResponseBody.getOrElse("{\"error\": \"No response\"}")
        val json = JsonMethods.parse(body)
        (json \ "error").toOption match {
          case Some(_) => deferred.failure(new OAuthAPIException(json))
          case None => deferred.success(AccessToken(json))
        }
      }
      def onThrowable(t: Throwable): Unit = {
        deferred.failure(t)
      }
    })
    deferred.future
  }
}

object OAuthAPI {
  def apply(clientId: String, clientSecret: String, redirectUri: String)(implicit client: Transport) = new OAuthAPI(clientId, clientSecret, redirectUri, client)

}
