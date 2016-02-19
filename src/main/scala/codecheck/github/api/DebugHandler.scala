package codecheck.github.api

import org.json4s.JValue
import java.io.PrintStream

trait DebugHandler {
  def onRequest(method: String, path: String, body: JValue): Unit
  def onResponse(status: Int, body: Option[String]): Unit
}

object NoneHandler extends DebugHandler {
  def onRequest(method: String, path: String, body: JValue): Unit = {}
  def onResponse(status: Int, body: Option[String]): Unit = {}
}

class PrintlnHandler(out: PrintStream = System.out) extends DebugHandler {
  def onRequest(method: String, path: String, body: JValue): Unit = {
    out.println(s"onRequest: $method $path $body")
  }
  def onResponse(status: Int, body: Option[String]): Unit = {
    out.println(s"onResponse: $status $body")
  }
}
