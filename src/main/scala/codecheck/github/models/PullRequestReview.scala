package codecheck.github
package models

import org.json4s.JsonDSL._
import org.json4s.JNull
import org.json4s.JValue

case class PullRequestReviewInput(
  body: Option[String] = None,
  event: Option[PullRequestReviewStateInput] = None,
  comments: Seq[PullRequestReviewCommentInput] = Seq.empty[PullRequestReviewCommentInput]
) extends AbstractInput {
  override val value: JValue = {
    ("body" -> body) ~
    ("event" -> event.map(_.name)) ~
    ("comments" -> comments.map(_.value))
  }
}

case class PullRequestReviewCommentInput(
  path: String,
  position: Long,
  body: String
) extends AbstractInput

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

sealed abstract class PullRequestReviewStateInput(val name: String)

object PullRequestReviewStateInput {
  case object APPROVE         extends PullRequestReviewStateInput("APPROVE")
  case object COMMENT         extends PullRequestReviewStateInput("COMMENT")
  case object PENDING         extends PullRequestReviewStateInput("PENDING")
  case object REQUEST_CHANGES extends PullRequestReviewStateInput("REQUEST_CHANGES")

  val values = Array(
    APPROVE,
    COMMENT,
    PENDING,
    REQUEST_CHANGES
  )

  def fromString(str: String) = values.filter(_.name == str).head
}

case class PullRequestReview(value: JValue) extends AbstractJson(value) {
  def id = get("id").toLong
  def body = get("body")
  def commit_id = get("commit_id")
  lazy val user = User(value \ "user")
  def state = PullRequestReviewState.fromString(get("state"))
  def submitted_at = dateOpt("submitted_at")
}
