package codecheck.github.models

import org.json4s.JValue
import org.json4s.JsonDSL._

class Organization(value: JValue) extends AbstractJson(value) {
  def login = get("login")
  def id = get("id").toLong
  def url = get("url")
  def avatar_url = get("avatar_url")
  def description = get("description")
}

case class OrganizationDetail(value: JValue) extends Organization(value) {
  def name = get("name")
  def company = opt("company")
  def email = get("email")
  def billing_email = get("billing_email")
  def location = get("location")
  def public_repos = get("public_repos").toInt
  def public_gists = get("public_gists").toInt
}

case class OrganizationInput(
  name: Option[String] = None,
  company: Option[String] = None,
  description: Option[String] = None,
  location: Option[String] = None,
  email: Option[String] = None,
  billing_email: Option[String] = None
) extends AbstractInput {
  override val value: JValue = {
    ("name" -> name) ~
    ("company" -> company) ~
    ("description" -> description) ~
    ("location" -> location) ~
    ("email" -> email) ~
    ("billing_email" -> email)
  }
}