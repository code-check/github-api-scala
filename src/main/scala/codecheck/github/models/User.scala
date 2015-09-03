package codecheck.github.models

import org.json4s.JValue
import codecheck.github.utils.ToDo
import org.json4s.JsonDSL._

case class User(value: JValue) extends AbstractJson(value) {
  def login: String = get("login")
  def id: Long = get("id").toLong
  def email: Option[String] = opt("email")
  def name: String = get("name")
  def blog: String = get("blog")
  def company: String = get("company")
  def location: String = get("location")
  def hireable: Boolean = boolean("hireable")
  def bio: Option[String] = opt("bio")
}

/*case class UserInput extends ToDo*/
case class UserInput (
	name: Option[String] = None,
	email: Option[String] = None,
	blog: Option[String] = None,
	company: Option[String] = None,
	location: Option[String] = None,
	hireable: Option[Boolean] = None,
	bio: Option[String] = None
) extends AbstractInput
