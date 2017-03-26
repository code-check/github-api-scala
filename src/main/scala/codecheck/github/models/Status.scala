package codecheck.github
package models

import org.json4s.JValue
import org.json4s.JArray

sealed abstract class StatusState(override val toString: String)

object StatusState {
  case object pending extends StatusState("pending")
  case object success extends StatusState("success")
  case object error   extends StatusState("error")
  case object failure extends StatusState("failure")

  val values = Array(pending, success, error, failure)

  def fromString(str: String) = values.filter(_.toString == str).head
}

case class Status(value: JValue) extends AbstractJson(value) {
  def state = StatusState.fromString(get("state"))
  def target_url = opt("target_url")
  def description = opt("description")
  def context = get("context")
  def id = get("id").toLong
  def url = get("url")
  def created_at = getDate("created_at")
  def updated_at = getDate("updated_at")
  lazy val assignee = objectOpt("assignee")(v => User(v))
}

case class StatusInput(
  state: StatusState,
  target_url: Option[String] = None,
  description: Option[String] = None,
  context: Option[String] = None
) extends AbstractInput {
  import org.json4s.JsonDSL._

  override val value: JValue =
    ("state" -> state.toString) ~
    ("target_url" -> target_url) ~
    ("description" -> description) ~
    ("context" -> context)
}

case class CombinedStatus(value: JValue) extends AbstractJson(value) {
  def state = StatusState.fromString(get("state"))
  def sha = get("sha")
  lazy val statuses = (value \ "statuses") match {
    case JArray(arr) => arr.map(Status(_))
    case _ => Nil
  }

  def repository = Repository(value \ "repository")
  def commit_url = get("commit_url")
  def total_count = get("total_count").toLong
  def url = get("url")
}
