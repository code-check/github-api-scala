package codecheck.github.models

import org.json4s.JValue

case class ReviewRequest(value: JValue) extends AbstractJson(value) {
  def id = get("id").toLong
  def number = get("number").toLong
}
