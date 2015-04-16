package codecheck.github.models

import org.json4s.JValue
import org.json4s.Formats
import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods

class AbstractJson(value: JValue) {
  private implicit val format: Formats = DefaultFormats

  def opt(path: String) = {
    Option(path.split("\\.").foldLeft(value) { (v, s) =>
      v \ s
    }.extract[String])
  }

  def get(path: String) = opt(path).get

  override def toString = JsonMethods.pretty(value)
}
