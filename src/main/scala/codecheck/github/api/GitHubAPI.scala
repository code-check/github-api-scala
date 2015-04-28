package codecheck.github.api

import com.ning.http.client.AsyncHttpClient
import com.ning.http.client.AsyncCompletionHandler
import com.ning.http.client.Response
import scala.concurrent.Promise
import scala.concurrent.Future
import java.net.URLEncoder
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

  protected def encode(s: String) = URLEncoder.encode(s, "utf-8").replaceAll("\\+", "%20")

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
      request.setBody(JsonMethods.compact(body))
    }
    request
      .setHeader("Authorization", s"token ${token}")
      .setHeader("Content-Type", "application/json")
    request.execute(new AsyncCompletionHandler[Response]() {
      def onCompleted(res: Response) = {
        val json = Option(res.getResponseBody).filter(_.length > 0).map(parseJson(_)).getOrElse(JNothing)
        if (res.getStatusCode == 404) {
          deferred.failure(new NotFoundException(json))
        } else {
          val result = APIResult(res.getStatusCode, json)
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
  def repositoryAPI(owner: String, repo: String) = RepositoryAPI(this, owner, repo)

  def close = client.close
}

object GitHubAPI {
  def apply(token: String)(implicit client: AsyncHttpClient) = new GitHubAPI(token, client)
}