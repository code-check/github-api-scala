package codecheck.github.models

import org.json4s.JValue

case class Repository(value: JValue) extends AbstractJson(value) {
  def id = get("id").toLong
  def name = get("name")

  lazy val owner = new User(value \ "owner")
}

