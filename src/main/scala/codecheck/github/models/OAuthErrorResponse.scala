package codecheck.github.models

import org.json4s.JValue

case class OAuthErrorResponse(value: JValue) extends AbstractJson(value) {
  def error = get("error")
  def error_description = get("error_description")
  def error_uri = get("error_uri")
}

