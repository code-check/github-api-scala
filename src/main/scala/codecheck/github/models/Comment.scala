package codecheck.github.models

import org.json4s.JValue

class Comment(value: JValue) extends AbstractJson(value) {
  def body = get("body")
}

