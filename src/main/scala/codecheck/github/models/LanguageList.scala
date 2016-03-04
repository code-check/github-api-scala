package codecheck.github.models

import org.json4s.{JValue, JObject}
import org.json4s.jackson.JsonMethods
import codecheck.github.utils.Json4s.formats

case class LanguageItem(name: String, bytes: Long, rate: Double)

case class LanguageList(value: JValue) extends AbstractJson(value) {

  lazy val items: List[LanguageItem] = {
    value match {
      case JObject(fields) =>
        val temp = fields.map { case (name, bytes) =>
          LanguageItem(name, bytes.extract[Long], 0.0)
        }
        val total = temp.map(_.bytes).sum
        temp.map { v =>
          val r = v.bytes.toDouble / total
          v.copy(rate = r)
        }
      case _ => Nil
    }
  }
}
