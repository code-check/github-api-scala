package codecheck.github.models

import org.json4s.JValue
import org.json4s.JString
import org.json4s.JNothing
import org.json4s.JNull
import org.json4s.JInt
import org.json4s.JArray
import org.json4s.JsonDSL._
import org.joda.time.DateTime
import org.joda.time.DateTimeZone

import codecheck.github.utils.ToDo

sealed abstract class IssueState(val name: String) {
  override def toString = name
}

object IssueState {
  case object open extends IssueState("open")
  case object closed extends IssueState("closed")
  case object all extends IssueState("all")

  val values = Array(open, closed, all)

  def fromString(str: String) = values.filter(_.name == str).head
}

sealed abstract class IssueFilter(val name: String) {
  override def toString = name
}

object IssueFilter {
  case object assigned extends IssueFilter("assigned")
  case object created extends IssueFilter("created")
  case object mentioned extends IssueFilter("mentioned")
  case object subscribed extends IssueFilter("subscribed")
  case object all extends IssueFilter("all")

  val values = Array(assigned, created, mentioned, subscribed, all)

  def fromString(str: String) = values.filter(_.name == str).head
}

sealed abstract class IssueSort(val name: String) {
  override def toString = name
}

object IssueSort {
  case object created extends IssueSort("created")
  case object updated extends IssueSort("updated")
  case object comments extends IssueSort("comments")

  val values = Array(created, updated, comments)

  def fromString(str: String) = values.filter(_.name == str).head
}

sealed abstract class MilestoneSearchOption(val name: String) {
  override def toString = name
}

object MilestoneSearchOption {
  case object all extends MilestoneSearchOption("*")
  case object none extends MilestoneSearchOption("none")
  case class Specified(number: Int) extends MilestoneSearchOption(number.toString())

  def apply(number: Int) = Specified(number)
}

case class IssueListOption(
  filter: IssueFilter = IssueFilter.assigned,
  state: IssueState = IssueState.open,
  labels: Seq[String] = Nil,
  sort: IssueSort = IssueSort.created,
  direction: SortDirection = SortDirection.desc,
  since: Option[DateTime] = None
) {
  def q = s"?filter=$filter&state=$state&sort=$sort&direction=$direction" +
    (if (!labels.isEmpty) "&labels=" + labels.mkString(",") else "") +
    (if (!since.isEmpty) (since map ("&since=" + _.toDateTime(DateTimeZone.UTC).toString("yyyy-MM-dd'T'HH:mm:ss'Z'"))).get else "")
}

case class IssueListOption4Repository(
  milestone: Option[MilestoneSearchOption] = None,
  state: IssueState = IssueState.open,
  assignee: Option[String] = None,
  creator: Option[String] = None,
  mentioned: Option[String] = None,
  labels: Seq[String] = Nil,
  sort: IssueSort = IssueSort.created,
  direction: SortDirection = SortDirection.desc,
  since: Option[DateTime] = None
) {
    def q = "?" + (if (!milestone.isEmpty) (milestone map (t => s"milestone=$t&")).get else "") +
      s"state=$state" +
      (if (!assignee.isEmpty) (assignee map (t => s"&assignee=$t")).get else "") +
      (if (!creator.isEmpty) (creator map (t => s"&creator=$t")).get else "") +
      (if (!mentioned.isEmpty) (mentioned map (t => s"&mentioned=$t")).get else "") +
      (if (!labels.isEmpty) "&labels=" + labels.mkString(",") else "") +
      s"&sort=$sort" +
      s"&direction=$direction" +
      (if (!since.isEmpty) (since map ("&since=" + _.toDateTime(DateTimeZone.UTC).toString("yyyy-MM-dd'T'HH:mm:ss'Z'"))).get else "")
 }

case class IssueInput(
  title: Option[String] = None,
  body: Option[String] = None,
  assignee: Option[String] = None,
  milestone: Option[Int] = None,
  labels: Seq[String] = Nil,
  state: Option[IssueState] = None
) extends AbstractInput {
  override val value: JValue = {
    val a = assignee.map { s =>
      if (s.length == 0) JNull else JString(s)
    }.getOrElse(JNothing)
    val l = if (labels.length == 0) JNothing else JArray(labels.map(JString(_)).toList)

    ("title" -> title) ~
    ("body" -> body) ~
    ("assignee" -> a) ~
    ("milestone" -> milestone) ~
    ("labels" -> l) ~
    ("state" -> state.map(_.name))
  }
}

object IssueInput {
  def apply(title: String, body: Option[String], assignee: Option[String], milestone: Option[Int], labels: Seq[String]): IssueInput =
    IssueInput(Some(title), body, assignee, milestone, labels, None)
}

case class Issue(value: JValue) extends AbstractJson(value) {
  def url = get("url")
  def labels_url = get("labels_url")
  def comments_url = get("comments_url")
  def events_url = get("events_url")
  def html_url = get("html_url")
  def id = get("id").toLong
  def number = get("number").toLong
  def title = get("title")

  lazy val user = new User(value \ "user")
  lazy val labels = (value \ "labels") match {
    case JArray(arr) => arr.map(new Label(_))
    case _ => Nil
  }

  def state = get("state")
  def locked = boolean("locked")

  lazy val assignee = objectOpt("assignee")(v => User(v))
  lazy val milestone = objectOpt("milestone")(v => Milestone(v))

  def comments = get("comments").toInt
  def created_at = getDate("created_at")
  def updated_at = getDate("updated_at")
  def closed_at = dateOpt("closed_at")
  def body = opt("body")

  lazy val closed_by = objectOpt("closed_by")(v => User(v))

  lazy val repository = new Repository(value \ "repository")
}
