package codecheck.github.models

import org.json4s.JValue

case class PullRequestInput(
  title: String,
  head: String,
  base: String,
  body: Option[String]
) extends AbstractInput

sealed abstract class PullRequestAction(val name: String) {
  override def toString = name
}

object PullRequestAction {
  case object assigned    extends PullRequestAction("assigned")
  case object unassigned  extends PullRequestAction("unassigned")
  case object labeled     extends PullRequestAction("labeled")
  case object unlabeled   extends PullRequestAction("unlabeled")
  case object opened      extends PullRequestAction("opened")
  case object edited      extends PullRequestAction("edited")
  case object closed      extends PullRequestAction("closed")
  case object reopened    extends PullRequestAction("reopened")
  case object synchronize extends PullRequestAction("synchronize")

  val values = Array(
    assigned,
    unassigned,
    labeled,
    unlabeled,
    opened,
    closed,
    reopened,
    synchronize
  )

  def fromString(str: String) = values.filter(_.name == str).head
}

case class PullRequestListOption(
  state: IssueState = IssueState.open,
  head: Option[String] = None,
  base: Option[String] = None,
  sort: IssueSort = IssueSort.created,
  direction: SortDirection = SortDirection.desc
)

case class PullRequestRef(value: JValue) extends AbstractJson(value) {
  def label = get("label")
  def ref = get("ref")
  def sha = get("sha")
  lazy val user = User(value \ "user")
  lazy val repo = Repository(value \ "repo")
}

case class PullRequest(value: JValue) extends AbstractJson(value) {
  def number = get("number").toLong
  def body = get("body")
  def state = IssueState.fromString(get("state"))
  def title = get("title")
  lazy val head = PullRequestRef(value \ "head")
  lazy val base = PullRequestRef(value \ "base")
}

