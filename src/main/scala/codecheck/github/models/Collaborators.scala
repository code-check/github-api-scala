package codecheck.github.models

import org.json4s.JValue
import org.json4s.jackson.JsonMethods

case class Collaborators(value: JValue) extends AbstractJson(value) {
  def login = get("login")
  def id = get("id").toLong
  def avatar_url = get("avatar_url")
  def url = get("url")
  def description = get("description")
  def site_admin = get("site_admin")
}

//case class CollaboratorsInput extends AbstractInput
