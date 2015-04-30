package codecheck.github.events

import org.json4s.JValue
import codecheck.github.models.AbstractJson
import codecheck.github.models.PullRequest
import codecheck.github.models.PullRequestAction

case class PullRequestEvent(name: String, value: JValue) extends AbstractJson(value) with GitHubEvent {
  def number = get("number").toLong

  lazy val action = PullRequestAction.fromString(get("action"))
  lazy val pull_request = PullRequest(value \ "pull_request")
}
