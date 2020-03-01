package codecheck.github.models

import org.json4s.JValue
import org.json4s.JsonDSL._

/*
## Github API response differently to organization requests depending on your involvement

# listOwnOrganization and listUserOrganization returns only fields in the Organization class

# updateOrganization will return all fields as you must be a member to update

# getOrganization will return the fields in OrganizationDetail if you are a member
If you are not a member of the organization, the following fields will NOT be returned
- total_private_repos (int)
- owned_private_repos (int)
- private_gists (int)
- disk_usage (long)
- collaborators (int)
- billing_email (string)
- plan (object -> name, space, private_repos)

## The following fields are optional (can be empty)
- name (string)
- email (string)
- description (string)
- location (string)
- blog (string)
- company (string)
*/


class Organization(value: JValue) extends AbstractJson(value) {
  def login = get("login")
  def id = get("id").toLong
  def url = get("url")
  def repos_url = get("repos_url")
  def events_url = get("events_url")
  def members_url = get("members_url")
  def public_members_url = get("public_members_url")
  def avatar_url = get("avatar_url")
  def description = get("description")
}

case class OrganizationDetail(value: JValue) extends Organization(value) {
  def name = get("name")
  def company = opt("company")
  def blog = get("blog")
  def location = get("location")
  def email = get("email")
  def public_repos = get("public_repos").toInt
  def public_gists = get("public_gists").toInt
  def followers = get("followers").toInt
  def following = get("following").toInt
  def html_url = get("html_url")
  def created_at = getDate("created_at")
  def updated_at = getDate("updated_at")
  def `type` = get("type")
}

case class OrganizationInput(
  name: Option[String] = None,
  company: Option[String] = None,
  description: Option[String] = None,
  location: Option[String] = None,
  email: Option[String] = None,
  billing_email: Option[String] = None
) extends AbstractInput
