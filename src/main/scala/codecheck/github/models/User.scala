package codecheck.github.models

import org.json4s.JValue

case class User(value: JValue) extends AbstractJson(value) {
  def login = get("login")
}

