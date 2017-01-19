package codecheck.github
package operations

import org.json4s.JArray

import codecheck.github.models.CombinedStatus
import codecheck.github.models.Status
import codecheck.github.models.StatusInput

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait StatusOp {
  self: api.GitHubAPI =>

  def createStatus(owner: String, repo: String, sha: String, input: StatusInput): Future[Status] = {
    val path = s"/repos/$owner/$repo/statuses/$sha"
    exec("POST", path, input.value).map { result =>
      Status(result.body)
    }
  }

  def listStatus(owner: String, repo: String, sha: String): Future[List[Status]] = {
    val path = s"/repos/$owner/$repo/commits/$sha/statuses"
    exec("GET", path, fail404 = false).map { result =>
      result.statusCode match {
        case 404 => Nil
        case _ => result.body match {
          case JArray(arr) => arr.map(Status(_))
          case _ => throw new IllegalStateException()
        }
      }
    }
  }

  def getStatus(owner: String, repo: String, sha: String): Future[CombinedStatus] = {
    val path = s"/repos/$owner/$repo/commits/$sha/status"
    exec("GET", path).map { result =>
      CombinedStatus(result.body)
    }
  }
}
