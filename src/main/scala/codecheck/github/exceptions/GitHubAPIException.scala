package codecheck.github.exceptions

import org.json4s.JValue
import codecheck.github.models.ErrorResponse

class GitHubAPIException(body: JValue) extends Exception {
  lazy val error = ErrorResponse(body)

  override def getMessage = error.toString

}