package codecheck.github.models
import org.json4s.JValue

case class Collaborator(value: JValue) extends AbstractJson(value) {
  def login = get("login")
  def id = get("id").toLong
  def avatar_url = get("avatar_url")
  def url = get("url")
  def site_admin: Boolean = boolean("site_admin")
}
//case class CollaboratorInput extends AbstractInput