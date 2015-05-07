package codecheck.github.models

import org.json4s.JValue
import codecheck.github.utils.ToDo

case class User(value: JValue) extends AbstractJson(value) {
  def login: String = get("login")
  def id: Long = get("id").toLong
  def email: String = get("email")
}

/*case*/ class UserInput extends ToDo

