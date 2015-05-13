package codecheck.github.models

import org.json4s.JValue
import codecheck.github.utils.ToDo
import org.json4s.JsonDSL._

case class User(value: JValue) extends AbstractJson(value) {
  def login: String = get("login")
  def id: Long = get("id").toLong
  def email: String = get("email")
  def name: String = get("name")
  def blog: String = get("blog")
  def company: String = get("company")
  def location: String = get("location")
  def hireable: String = get("hireable")
  def bio: String = get("bio")
}

/*case class UserInput extends ToDo*/
case class UserInput (
	name: Option[String] = None,
	email: Option[String] = None,
	blog: Option[String] = None,
	company: Option[String] = None,
	location: Option[String] = None,
	hireable: Option[String] = None,
	bio: Option[String] = None
) extends AbstractInput

object UserInput {
  def apply(name: String): UserInput =
    UserInput(Some(name), None, None, None, None, None, None)
  def apply(name: String, email: String): UserInput =
    UserInput(Some(name), Some(email), None, None, None, None, None)
  def apply(name: String, email: String,blog: String): UserInput =
    UserInput(Some(name), Some(email), Some(blog), None, None, None, None)
  def apply(name: String, email: String,blog: String,company: String): UserInput =
    UserInput(Some(name), Some(email), Some(blog), Some(company), None, None, None)
  def apply(name: String, email: String,blog: String,company: String, location: String): UserInput =
    UserInput(Some(name), Some(email), Some(blog), Some(company), Some(location), None, None)
  def apply(name: String, email: String,blog: String,company: String, location: String, hireable: String): UserInput =
    UserInput(Some(name), Some(email), Some(blog), Some(company), Some(location), Some(hireable), None)
  def apply(name: String, email: String,blog: String,company: String, location: String, hireable: String, bio: String): UserInput =
    UserInput(Some(name), Some(email), Some(blog), Some(company), Some(location), Some(hireable), Some(bio))
}