package codecheck.github.models

import org.json4s.JValue
import org.json4s.JString
import org.json4s.JNothing
import org.json4s.JNull
import org.json4s.JArray
import org.json4s.JsonDSL._

sealed abstract class IssueState(val name: String) {
  override def toString = name
}

object IssueState {
  case object Open extends IssueState("open")
  case object Closed extends IssueState("closed")
}

case class IssueEditParams(
  title: Option[String] = None,
  body: Option[String] = None,
  assignee: Option[String] = None,
  milestone: Option[Int] = None,
  labels: List[String] = Nil,
  state: Option[IssueState] = None
) {
  def toJson: JValue = {
    val a = assignee.filter(_ == "none")
      .map(_ => JNull)
      .getOrElse(assignee.map(JString(_)).getOrElse(JNothing))

    ("title" -> title) ~
    ("body" -> body) ~
    ("assignee" -> a) ~
    ("milestone" -> milestone) ~
    ("labels" -> (if (labels.length == 0) JNothing else JArray(labels.map(JString(_))))) ~
    ("state" -> state.map(_.name))
  }
}

class Issue(value: JValue) extends AbstractJson(value) {
  //ToDo
}