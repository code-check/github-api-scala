package codecheck.github.models

import org.json4s.JValue
import org.json4s.JNothing
import org.json4s.JNull
import org.json4s.Formats
import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods
import codecheck.github.utils.Json4s.formats

import org.joda.time.DateTime

class AbstractJson(value: JValue) {

  def opt(path: String): Option[String] = {
    path.split("\\.").foldLeft(value) { (v, s) =>
      v \ s
    } match {
      case JNothing => None
      case JNull => None
      case v: JValue => Some(v.extract[String])
    }
  }

  def get(path: String) = opt(path).get

  def dateOpt(path: String): Option[DateTime] = {
    path.split("\\.").foldLeft(value) { (v, s) =>
      v \ s
    } match {
      case JNothing => None
      case JNull => None
      case v: JValue => Some(v.extract[DateTime])
    }
  }

  def date(path: String): DateTime = dateOpt(path).get

  override def toString = JsonMethods.pretty(value)
}
