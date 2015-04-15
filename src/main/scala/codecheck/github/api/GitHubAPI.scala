package codecheck.github.api

import com.ning.http.client.AsyncHttpClient
import com.ning.http.client.AsyncCompletionHandler
import com.ning.http.client.Response
import scala.concurrent.Promise
import scala.concurrent.Future
import org.json4s.JValue
import org.json4s.JNothing
    import org.json4s.jackson.JsonMethods

class GitHubAPI(token: String) {
  private val endpoint = "https://api.github.com"
  private val client = new AsyncHttpClient()

  private def parseJson(json: String) = {
    JsonMethods.parse(json)
  }

  def exec(method: String, path: String, body: JValue = JNothing): Future[APIResult] = {
    val deferred = Promise[APIResult]()
    val url = endpoint + path
    val request = method match {
      case "GET" => client.prepareGet(url)
      case "POST" => client.preparePost(url)
      case "PUT" => client.preparePut(url)
      case "DELETE" => client.prepareDelete(url)
    }
    if (body != JNothing) {
      request.setBody(body.toString)
    }
    request
      .setHeader("Authorization", s"token ${token}")
      .setHeader("Content-Type", "application/json")
    request.execute(new AsyncCompletionHandler[Response]() {
      def onCompleted(res: Response) = {
        val result = APIResult(res.getStatusCode, parseJson(res.getResponseBody))
        deferred.success(result)
        res
      }
      override def onStatusReceived(status: com.ning.http.client.HttpResponseStatus) = {
        super.onStatusReceived(status)
      }
      override def onThrowable(t: Throwable) {
        deferred.failure(t)
        super.onThrowable(t)
      }
    })
    deferred.future
  }
  def repository(owner: String, repo: String) = new GitHubRepository(this, owner, repo)

  def org(user: String) = exec("GET", s"/users/${user}/orgs") 
}

object GitHubAPI {
  def apply(token: String) = new GitHubAPI(token)
}