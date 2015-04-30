package codecheck.github.utils

import org.json4s._
import org.json4s.jackson.JsonMethods._

object Json4s {
  implicit val formats = DefaultFormats ++ org.json4s.ext.JodaTimeSerializers.all
}

