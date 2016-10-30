package codecheck.github.operations

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import org.json4s.JArray
import org.json4s.JObject
import org.json4s.JString

import codecheck.github.api.GitHubAPI
import codecheck.github.models.PullRequestInput
import codecheck.github.models.PullRequestListOption
import codecheck.github.models.PullRequest

trait PullRequestOp {
  self: GitHubAPI =>

  def listPullRequests(
    owner: String,
    repo: String,
    option: PullRequestListOption = PullRequestListOption()
  ): Future[List[PullRequest]] = {
    val q = s"?state=${option.state}" +
      s"&sort=${option.sort}" +
      s"&direction=${option.direction}" +
      option.head.map("&head=" + _).getOrElse("") +
      option.base.map("&base=" + _).getOrElse("")

    exec("GET", s"/repos/$owner/$repo/pulls$q").map(
      _.body match {
        case JArray(arr) => arr.map(v => PullRequest(v))
        case _ => throw new IllegalStateException()
      }
    )
  }

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
