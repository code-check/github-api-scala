package codecheck.github.events

import org.json4s.JValue
import org.json4s.JArray
import codecheck.github.models.AbstractJson
import codecheck.github.models.PullRequest
import codecheck.github.models.PullRequestAction
import codecheck.github.models.Repository
import codecheck.github.models.User

case class PushCommit(value: JValue) extends AbstractJson(value) {
  def id = get("id")
  def url = get("url")
  def tree_id = get("tree_id")
  def distinct = boolean("distinct")
  def message = get("message")
  def timestamp = get("timestamp")
  lazy val author = User(value \ "author")
  lazy val committer = User(value \ "committer")
  def added = seq("added")
  def removed = seq("removed")
  def modified = seq("modified")
}

case class PushEvent(name: String, value: JValue) extends AbstractJson(value) with GitHubEvent {
  def ref = get("ref")
  def before = get("before")
  def after = get("after")
  lazy val base_ref = opt("base_ref")
  lazy val head_commit = PushCommit(value \ "head_commit")
  lazy val commits = (value \ "commits") match {
    case JArray(arr) => arr.map(PushCommit(_))
    case _ => Seq.empty[PushCommit]
  }
  lazy val repository = Repository(value \ "repository")
  lazy val pusher = User(value \ "pusher")
  lazy val sender = User(value \ "sender")
}
