package codecheck.github.models

import org.json4s.JValue

class Webhook(value: JValue) extends AbstractJson(value) {
  def repository = get("repository")
  def sender = get("sender")
}

case class WebhookInput(value: JValue) extends Webhook(value) {
  def name = get("name")
  def company = opt("company")
  def email = get("email")
  def billing_email = get("billing_email")
  def location = get("location")
  def public_repos = get("public_repos").toInt
  def public_gists = get("public_gists").toInt
}
