package codecheck.github.models

import org.json4s.JValue

class User(value: JValue) extends AbstractJson(value) {
  def name: String = opt("name").getOrElse(get("login"))
}

