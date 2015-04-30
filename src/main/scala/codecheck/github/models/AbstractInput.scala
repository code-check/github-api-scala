package codecheck.github.models

import org.json4s._
import org.json4s.jackson.JsonMethods
import codecheck.github.utils.Json4s.formats

trait AbstractInput {
  val value: JValue = Extraction.decompose(this)
  override def toString = JsonMethods.pretty(value)
}
