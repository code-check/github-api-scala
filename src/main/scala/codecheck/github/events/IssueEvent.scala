package codecheck.github.events

import org.json4s.JValue
import codecheck.github.models.AbstractJson
import codecheck.github.models.Issue
import codecheck.github.models.IssueAction
import codecheck.github.models.Comment

case class IssueEvent(name: String, value: JValue) extends AbstractJson(value) with GitHubEvent {

  lazy val action = IssueAction.fromString(get("action"))
  lazy val issue = Issue(value \ "issue")
}
