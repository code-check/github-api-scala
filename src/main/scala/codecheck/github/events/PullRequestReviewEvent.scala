package codecheck.github
package events

import org.json4s.JValue
import codecheck.github.models.AbstractJson
import codecheck.github.models.PullRequest
import codecheck.github.models.PullRequestReview
import codecheck.github.models.PullRequestReviewAction
import codecheck.github.models.Repository
import codecheck.github.models.User

case class PullRequestReviewEvent(name: String, value: JValue) extends AbstractJson(value) with GitHubEvent {
  lazy val action = PullRequestReviewAction.fromString(get("action"))
  lazy val review = PullRequestReview(value \ "review")
  lazy val pull_request = models.PullRequest(value \ "pull_request")
  lazy val repository = new Repository(value \ "repository")
  lazy val sender = new User(value \ "sender")
}
