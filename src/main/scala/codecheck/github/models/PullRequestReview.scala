package codecheck.github
package models

import org.json4s.JValue

sealed abstract class PullRequestReviewAction(val name: String) {
  override def toString = name
}

object PullRequestReviewAction {
  case object submitted    extends PullRequestReviewAction("submitted")

  val values = Array(
    submitted
  )

  def fromString(str: String) = values.filter(_.name == str).head
}

sealed abstract class PullRequestReviewState(val name: String) {
  override def toString = name
}

object PullRequestReviewState {
  case object approved    extends PullRequestReviewState("approved")

  val values = Array(
    approved
  )

  def fromString(str: String) = values.filter(_.name == str).head
}

case class PullRequestReview(value: JValue) extends AbstractJson(value) {
  def id = get("id").toLong
  def body = opt("body")
  def commit_id = get("commit_id")
  lazy val user = User(value \ "user")
  def state = PullRequestReviewState.fromString(get("state"))
  def submitted_at = dateOpt("submitted_at")
}
