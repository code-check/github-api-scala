package codecheck.github.api

import org.json4s.JValue

case class APIResult(statusCode: Int, body: JValue)
