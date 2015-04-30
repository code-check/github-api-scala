package codecheck.github.models

import org.json4s.JValue
import org.json4s.JsonDSL._
import org.joda.time.DateTime

sealed abstract class MilestoneState(val name: String) {
  override def toString = name
}

object MilestoneState {
  case object open extends MilestoneState("open")
  case object closed extends MilestoneState("closed")
  case object all extends MilestoneState("all")

  val values = Array(open, closed, all)

  def fromString(str: String) = values.filter(_.name == str).head
}

sealed abstract class MilestoneSort(val name: String) {
  override def toString = name
}

object MilestoneSort {
  case object due_date extends MilestoneSort("due_date")
  case object completeness extends MilestoneSort("completeness")

  val values = Array(due_date, completeness)

  def fromString(str: String) = values.filter(_.name == str).head
}

case class MilestoneListOption(
  state: MilestoneState = MilestoneState.open, 
  sort: MilestoneSort = MilestoneSort.due_date,
  direction: SortDirection = SortDirection.asc
)

case class MilestoneInput(
  title: Option[String] = None,
  state: Option[MilestoneState] = None,
  description: Option[String] = None,
  due_on: Option[DateTime] = None
) extends AbstractInput {
  override val value: JValue = {
    ("title" -> title) ~
    ("state" -> state.map(_.name)) ~
    ("description" -> description) ~
    ("due_on" -> due_on.map(_.toString("yyyy-MM-dd'T'HH:mm:ssZ")))
  }
}

object MilestoneInput {
  def apply(title: String): MilestoneInput =
    MilestoneInput(Some(title), None, None, None)
  def apply(title: String, description: String): MilestoneInput =
    MilestoneInput(Some(title), None, Some(description))
  def apply(title: String, due_on: DateTime): MilestoneInput =
    MilestoneInput(Some(title), None, None, Some(due_on))
  def apply(title: String, description: String, due_on: DateTime): MilestoneInput =
    MilestoneInput(Some(title), None, Some(description), Some(due_on))
}

case class Milestone(value: JValue) extends AbstractJson(value) {
  def url = get("url")
  def id = get("id").toLong
  def number = get("number").toInt
  lazy val state = MilestoneState.fromString(get("state"))
  def title = get("title")
  def description = opt("description")
  lazy val creator = User(value \ "creator")
  def open_issues = get("open_issues").toInt
  def closed_issues = get("closed_issues").toInt
  def created_at = date("created_at")
  def updated_at = date("updated_at")
  def closed_at = dateOpt("closed_at")
  def due_on = dateOpt("due_on")
}
