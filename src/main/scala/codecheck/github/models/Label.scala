package codecheck.github.models

import org.json4s.JValue
import org.json4s.jackson.JsonMethods

case class Label(value: JValue) extends AbstractJson(value) {
  def url = opt("url")
  def name = get("name")
  def color = get("color")
}

object Label {
  def apply(name: String, color: String): Label = {
    val json = s"""
      { "name": "$name", "color": "$color"}
    """
    new Label(JsonMethods.parse(json))
  }
}
