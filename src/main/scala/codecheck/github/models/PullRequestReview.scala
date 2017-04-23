package codecheck.github
package models

import org.json4s.JValue

sealed abstract class PullRequestReviewAction(val name: String) {
  override def toString = name
}

object PullRequestReviewAction {
  case object submitted extends PullRequestReviewAction("submitted")
  case object edited    extends PullRequestReviewAction("edited")
  case object dismissed extends PullRequestReviewAction("dismissed")

  val values = Array(
    submitted,
    edited,
    dismissed
  )

  def fromString(str: String) = values.filter(_.name == str).head
}

sealed abstract class PullRequestReviewState(val name: String) {
  override def toString = name
}

object PullRequestReviewState {
  case object approved  extends PullRequestReviewState("approved")
  case object dismissed extends PullRequestReviewState("dismissed")
  case object pending   extends PullRequestReviewState("pending")
  case object changes_requested   extends PullRequestReviewState("changes_requested")

  val values = Array(
    approved,
    dismissed,
    pending,
    changes_requested
  )

  def fromString(str: String) = values.filter(_.name == str.toLowerCase).head
}

case class PullRequestReview(value: JValue) extends AbstractJson(value) {
  def id = get("id").toLong
  def body = get("body")
  def commit_id = get("commit_id")
  lazy val user = User(value \ "user")
  def state = PullRequestReviewState.fromString(get("state"))
  def submitted_at = dateOpt("submitted_at")
}
