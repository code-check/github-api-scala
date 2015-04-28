package codecheck.github.models

import org.json4s.JValue
import org.json4s.jackson.JsonMethods

case class Label(value: JValue) extends AbstractJson(value) {
  def url = get("url")
  def name = get("name")
  def color = get("color")
}

case class LabelInput(name: String, color: String) extends AbstractInput

