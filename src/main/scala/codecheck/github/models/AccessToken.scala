package codecheck.github.models

import org.json4s.JValue

class AccessToken(value: JValue) extends AbstractJson(value) {
  def access_token = get("access_token")
  def token_type = get("token_type")
  def scope: List[String] = get("scope").split(",").toList
}

