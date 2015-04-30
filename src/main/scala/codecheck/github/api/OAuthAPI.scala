package codecheck.github.api

import com.ning.http.client.AsyncHttpClient
import com.ning.http.client.AsyncCompletionHandler
import com.ning.http.client.Response
import com.ning.http.client.RequestBuilder
import com.ning.http.util.UTF8UrlEncoder
import scala.concurrent.Promise
import scala.concurrent.Future
import org.json4s.jackson.JsonMethods
import org.json4s.DefaultFormats
import java.util.UUID
import codecheck.github.models.AccessToken

class OAuthAPI(clientId: String, clientSecret: String, redirectUri: String, client: AsyncHttpClient) {
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
    val query: String = params.map { case (k, v) => k +"="+ UTF8UrlEncoder.encode(v) }.mkString("&")
    accessRequestUri +"?"+ query
  }

  def requestToken(code: String): Future[AccessToken] = {
    val params: Map[String, String] = Map(
      "client_id" -> clientId,
      "client_secret" -> clientSecret,
      "code" -> code,
      "redirect_uri" -> redirectUri
    )
    val builder: RequestBuilder = new RequestBuilder("POST")
      .setHeader("Content-Type", "application/x-www-form-urlencoded")
      .setHeader("Accept", "application/json")
      .setFollowRedirects(true)
      .setUrl(tokenRequestUri)
    params.foreach { case (k, v) => builder.addParameter(k, v) }

    val deferred = Promise[AccessToken]()
    client.prepareRequest(builder.build).execute(new AsyncCompletionHandler[Response]() {
      def onCompleted(res: Response) = {
        val json = JsonMethods.parse(res.getResponseBody("utf-8"))
        deferred.success(AccessToken(json))
        res
      }
      override def onThrowable(t: Throwable) {
        deferred.failure(t)
        super.onThrowable(t)
      }
    })
    deferred.future
  }
}

object OAuthAPI {
  def apply(clientId: String, clientSecret: String, redirectUri: String)(implicit client: AsyncHttpClient) = new OAuthAPI(clientId, clientSecret, redirectUri, client)

}
