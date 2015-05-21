package codecheck.github.api

import scala.concurrent.Promise
import scala.concurrent.Future
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import java.net.URLEncoder
import java.util.Base64
import org.json4s.JValue
import org.json4s.JNothing
import org.json4s.jackson.JsonMethods

import codecheck.github.exceptions.PermissionDeniedException
import codecheck.github.exceptions.NotFoundException
import codecheck.github.exceptions.UnauthorizedException
import codecheck.github.exceptions.GitHubAPIException
import codecheck.github.operations._
import codecheck.github.models.User
import codecheck.github.transport._

class GitHubAPI(token: String, client: Transport, tokenType: String = "token", debugHandler: DebugHandler = NoneHandler) extends UserOp
  with OrganizationOp
  with RepositoryOp
  with LabelOp
  with IssueOp
  with PullRequestOp
  with MilestoneOp
  with WebhookOp
  with CollaboratorOp
  with BranchOp
  with SearchOp
{

  private val endpoint = "https://api.github.com"

  private def parseJson(json: String) = {
    JsonMethods.parse(json)
  }

  protected def encode(s: String) = URLEncoder.encode(s, "utf-8").replaceAll("\\+", "%20")

  def exec(method: String, path: String, body: JValue = JNothing, fail404: Boolean = true): Future[APIResult] = {
    debugHandler.onRequest(method, path, body)
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
    request.execute(new CompletionHandler() {
      def onCompleted(res: Response) = {
        debugHandler.onResponse(res.getStatusCode, res.getResponseBody)
        val json = res.getResponseBody.filter(_.length > 0).map(parseJson(_)).getOrElse(JNothing)
        res.getStatusCode match {
          case 401 =>
            deferred.failure(new UnauthorizedException(json))
          case 403 =>
            deferred.failure(new PermissionDeniedException(json))
          case 422 =>
            deferred.failure(new GitHubAPIException(json))
          case 404 if fail404 =>
            deferred.failure(new NotFoundException(json))
          case _ =>
            val result = APIResult(res.getStatusCode, json)
            deferred.success(result)
        }
      }
      def onThrowable(t: Throwable) {
        deferred.failure(t)
      }
    })
    deferred.future
  }

  lazy val user = Await.result(getAuthenticatedUser, Duration.Inf)

  def repositoryAPI(owner: String, repo: String) = RepositoryAPI(this, owner, repo)

  def close = client.close

  def withDebugHandler(dh: DebugHandler): GitHubAPI = new GitHubAPI(token, client, tokenType, dh)
}

object GitHubAPI {

  def apply(token: String)(implicit client: Transport): GitHubAPI = new GitHubAPI(token, client)

  def apply(username: String, password: String)(implicit client: Transport): GitHubAPI = {
    val token = Base64.getEncoder.encodeToString((username + ":" + password).getBytes("utf-8"))
    new GitHubAPI(token, client, "Basic")
  }
}
