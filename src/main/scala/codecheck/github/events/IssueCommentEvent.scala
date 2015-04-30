package codecheck.github.events

import org.json4s.JValue
import codecheck.github.models.Issue
import codecheck.github.models.Comment
import codecheck.github.models.AbstractJson

case class IssueCommentEvent(name: String, value: JValue) extends AbstractJson(value) with GitHubEvent {
  lazy val issue = Issue(value \ "issue")
  lazy val comment = Comment(value \ "comment")
}
