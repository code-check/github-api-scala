package codecheck.github.exceptions

import org.json4s.JValue
import codecheck.github.models.OAuthErrorResponse

class OAuthAPIException(body: JValue) extends Exception {
  lazy val error = OAuthErrorResponse(body)

  override def getMessage = error.toString

}
