package codecheck.github.api

import com.ning.http.client.AsyncHttpClient
import com.ning.http.client.AsyncCompletionHandler
import com.ning.http.client.Response
import scala.concurrent.Promise
import scala.concurrent.Future
import org.json4s.JValue
import org.json4s.JNothing
import org.json4s.jackson.JsonMethods

import codecheck.github.exceptions.NotFoundException
import codecheck.github.operations._

class GitHubAPI(token: String, client: AsyncHttpClient) extends OrganizationOp 
  with LabelOp
  with IssueOp
{

  private val endpoint = "https://api.github.com"

  private def parseJson(json: String) = {
    JsonMethods.parse(json)
  }

  def exec(method: String, path: String, body: JValue = JNothing): Future[APIResult] = {
    val deferred = Promise[APIResult]()
    val url = endpoint + path
    val request = method match {
      case "GET" => client.prepareGet(url)
      case "PATCH" => client.preparePost(url)
      case "POST" => client.preparePost(url)
      case "PUT" => client.preparePut(url)
      case "DELETE" => client.prepareDelete(url)
    }
    if (body != JNothing) {
println("body: " + JsonMethods.pretty(body) + ", " + body.toString)
      request.setBody(JsonMethods.compact(body))
    }
    request
      .setHeader("Authorization", s"token ${token}")
      .setHeader("Content-Type", "application/json")
    request.execute(new AsyncCompletionHandler[Response]() {
      def onCompleted(res: Response) = {
        if (res.getStatusCode == 404) {
          deferred.failure(new NotFoundException())
        } else {
          val result = APIResult(res.getStatusCode, parseJson(res.getResponseBody))
println("result: " + result)
          deferred.success(result)
        }
        res
      }
      override def onThrowable(t: Throwable) {
        deferred.failure(t)
        super.onThrowable(t)
      }
    })
    deferred.future
  }
  def repository(owner: String, repo: String) = RepositoryAPI(this, owner, repo)

  def close = client.close
}

object GitHubAPI {
  def apply(token: String)(implicit client: AsyncHttpClient) = new GitHubAPI(token, client)
}