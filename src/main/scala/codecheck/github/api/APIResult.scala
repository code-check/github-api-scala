package codecheck.github.api

import org.json4s.JValue
import org.json4s.jackson.JsonMethods

case class APIResult(statusCode: Int, body: JValue) {
  override def toString = {
    "status = " + statusCode + "\n" + JsonMethods.pretty(body)
  }
}
