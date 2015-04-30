package codecheck.github.models

import org.json4s.JValue

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
  def public_repos = get("public_repos").toInt
  def public_gists = get("public_gists").toInt
}
