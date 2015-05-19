package codecheck.github.api

import com.ning.http.client.AsyncHttpClient
import com.ning.http.client.AsyncCompletionHandler
import com.ning.http.client.Response
import scala.concurrent.Promise
import scala.concurrent.Future
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import java.net.URLEncoder
import java.util.Base64
import org.json4s.JValue
import org.json4s.JNothing
import org.json4s.jackson.JsonMethods

import codecheck.github.exceptions.NotFoundException
import codecheck.github.exceptions.UnauthorizedException
import codecheck.github.exceptions.GitHubAPIException
import codecheck.github.operations._
import codecheck.github.models.User

class GitHubAPI(token: String, client: AsyncHttpClient, tokenType: String = "token") extends UserOp
  with OrganizationOp
  with RepositoryOp
  with LabelOp
  with IssueOp
  with MilestoneOp
  with CollaboratorOp
{

  private val endpoint = "https://api.github.com"

  private def parseJson(json: String) = {
    JsonMethods.parse(json)
  }

  protected def encode(s: String) = URLEncoder.encode(s, "utf-8").replaceAll("\\+", "%20")

  def exec(method: String, path: String, body: JValue = JNothing, fail404: Boolean = true): Future[APIResult] = {
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
      .setHeader("Authorization", s"$tokenType $token")
      .setHeader("Content-Type", "application/json")
    if (method == "PUT" && body == JNothing){
      request
        .setHeader("Content-Length", "0")
    }
    request.execute(new AsyncCompletionHandler[Response]() {
      def onCompleted(res: Response) = {
        val json = Option(res.getResponseBody).filter(_.length > 0).map(parseJson(_)).getOrElse(JNothing)
        res.getStatusCode match {
          case 401 =>
            deferred.failure(new UnauthorizedException(json))
          case 422 =>
            deferred.failure(new GitHubAPIException(json))
          case 404 if fail404 =>
            deferred.failure(new NotFoundException(json))
          case _ =>
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

  lazy val user = Await.result(getAuthenticatedUser, Duration.Inf)

  def repositoryAPI(owner: String, repo: String) = RepositoryAPI(this, owner, repo)

  def close = client.close
}

object GitHubAPI {

  def fromEnv: GitHubAPI = {
    implicit val client = new AsyncHttpClient
    apply(sys.env("GITHUB_TOKEN"))
  }

  def apply(token: String)(implicit client: AsyncHttpClient): GitHubAPI = new GitHubAPI(token, client)

  def apply(username: String, password: String)(implicit client: AsyncHttpClient): GitHubAPI = {
    val token = Base64.getEncoder.encodeToString((username + ":" + password).getBytes("utf-8"))
    new GitHubAPI(token, client, "Basic")
  }
}
