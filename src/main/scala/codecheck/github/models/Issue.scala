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

case class IssueInput(
  title: Option[String] = None,
  body: Option[String] = None,
  assignee: Option[String] = None,
  milestone: Option[Int] = None,
  labels: List[String] = Nil,
  state: Option[IssueState] = None
) extends AbstractInput {
  override val value: JValue = {
    val a = assignee.map { s =>
      if (s.length == 0) JNull else JString(s)
    }.getOrElse(JNothing)
    val l = if (labels.length == 0) JNothing else JArray(labels.map(JString(_)))

    ("title" -> title) ~
    ("body" -> body) ~
    ("assignee" -> a) ~
    ("milestone" -> milestone) ~
    ("labels" -> l) ~
    ("state" -> state.map(_.name))
  }
}

class Issue(value: JValue) extends AbstractJson(value) {
  def number = get("number").toLong
  lazy val user = new User(value \ "user")
  lazy val labels = (value \ "labels") match {
    case JArray(arr) => arr.map(new Label(_))
    case _ => Nil
  }
}