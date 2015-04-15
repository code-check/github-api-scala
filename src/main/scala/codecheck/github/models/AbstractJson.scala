package codecheck.github.models

import org.json4s.JValue
import org.json4s.Formats
import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods

class AbstractJson(value: JValue) {
  private implicit val format: Formats = DefaultFormats

  protected def get(path: String) = {
    path.split("\\.").foldLeft(value) { (v, s) =>
      v \ s
    }.extract[String]
  }

  protected def opt(path: String) = Option(get(path))

  override def toString = JsonMethods.pretty(value)
}
