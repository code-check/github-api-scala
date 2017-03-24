package codecheck.github.operations

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import org.json4s.JArray
import org.json4s.JObject
import org.json4s.JString

import codecheck.github.api.GitHubAPI
import codecheck.github.models.PullRequestReviewInput
import codecheck.github.models.PullRequestReview

trait PullRequestReviewOp {
  self: GitHubAPI =>

  def listPullRequestReviews(
    owner: String,
    repo: String,
    number: Long
  ): Future[List[PullRequestReview]] = {
    exec("GET", s"/repos/$owner/$repo/pulls/$number/reviews").map(
      _.body match {
        case JArray(arr) => arr.map(v => PullRequestReview(v))
        case _ => throw new IllegalStateException()
      }
    )
  }

  def getPullRequestReview(owner: String, repo: String, number: Long, id: Long): Future[Option[PullRequestReview]] = {
    val path = s"/repos/$owner/$repo/pulls/$number/reviews/$id"
    exec("GET", path, fail404=false).map(res => 
      res.statusCode match {
        case 404 => None
        case 200 => Some(PullRequestReview(res.body))
      }
    )
  }

  def createPullRequestReview(owner: String, repo: String, number: Long, input: PullRequestReviewInput): Future[PullRequestReview] = {
    val path = s"/repos/$owner/$repo/pulls/$number/reviews"
    exec("POST", path, input.value).map { result =>
      PullRequestReview(result.body)
    }
  }

  def dismissPullRequestReview(owner: String, repo: String, number: Long, id: Long, message: String): Future[PullRequestReview] = {
    val path = s"/repos/$owner/$repo/pulls/$number/reviews/$id/dismissals"
    exec("PUT", path, JObject(List(
      "message" -> JString(message)
    ))).map { result =>
      new PullRequestReview(result.body)
    }
  }

}
