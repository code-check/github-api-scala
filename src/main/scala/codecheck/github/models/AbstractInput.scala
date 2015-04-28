package codecheck.github.models

import org.json4s._
import org.json4s.jackson.JsonMethods

trait AbstractInput {
  protected implicit val format: Formats = DefaultFormats

  val value: JValue = Extraction.decompose(this)
  override def toString = JsonMethods.pretty(value)
}
