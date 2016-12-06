package codecheck.github
package events

import org.json4s.JValue
import codecheck.github.models.AbstractJson
import codecheck.github.models.PullRequest
import codecheck.github.models.Review
import codecheck.github.models.PullRequestReviewAction

case class PullRequestReviewEvent(name: String, value: JValue) extends AbstractJson(value) with GitHubEvent {
  lazy val action = PullRequestReviewAction.fromString(get("action"))
  lazy val review = Review(value \ "review")
  lazy val pull_request = models.PullRequest(value \ "pull_request")
}
