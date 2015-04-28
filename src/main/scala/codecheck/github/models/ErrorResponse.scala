package codecheck.github.models

import org.json4s.JValue
import org.json4s.JArray
import org.json4s.JNothing
import org.json4s.jackson.JsonMethods

case class ErrorResponse(value: JValue) extends AbstractJson(value) {
  def message = get("message")
  lazy val errors: List[ErrorObject] = (value \ "errors") match {
    case JArray(ar) => ar.map(e => ErrorObject(e))
    case JNothing => Nil
    case _ => throw new IllegalStateException()
  }
  def documentation_url = opt("documentation_url")
}

case class ErrorObject(value: JValue) extends AbstractJson(value) {
  def resource = get("resource")
  def field = get("field")
  def code = get("code")
}