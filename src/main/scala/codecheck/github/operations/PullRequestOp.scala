package codecheck.github.operations

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import org.json4s.{JObject, JString}

import codecheck.github.api.GitHubAPI
import codecheck.github.models.PullRequestInput
import codecheck.github.models.PullRequest

trait PullRequestOp {
  self: GitHubAPI =>

  def createPullRequest(owner: String, repo: String, input: PullRequestInput): Future[PullRequest] = {
    val path = s"/repos/$owner/$repo/pulls"
    exec("POST", path, input.value).map { result =>
      PullRequest(result.body)
    }
  }

  def closePullRequest(owner: String, repo: String, number: Long): Future[PullRequest] = {
    val path = s"/repos/$owner/$repo/pulls/$number"
    exec("PATCH", path, JObject(List(
      "state" -> JString("close")
    ))).map { result =>
      new PullRequest(result.body)
    }
  }

}
