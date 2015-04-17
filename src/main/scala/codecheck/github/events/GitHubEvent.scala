package codecheck.github.events

import org.json4s.JValue
import org.json4s.jackson.JsonMethods
import codecheck.github.models.Repository

trait GitHubEvent {
  val name: String
  val value: JValue

  lazy val repository = new Repository(value \ "repository")
  override def toString = name + "\n" + JsonMethods.pretty(value)
}

object GitHubEvent {
  def apply(name: String, value: JValue): GitHubEvent = name match {
    case "issue" => IssueEvent(name, value)
    case "issue_comment" => IssueCommentEvent(name, value)
    case "pull_request" => PullRequestEvent(name, value)
    case _ => DefaultEvent(name, value)
  }
}

