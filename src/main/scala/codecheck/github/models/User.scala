package codecheck.github.models

import org.json4s.JValue
import codecheck.github.utils.ToDo

case class User(value: JValue) extends AbstractJson(value) {
  def login = get("login")
  def id = get("id")
  def email = get("email")
}

/*case*/ class UserInput extends ToDo

