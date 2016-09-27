package codecheck.github.transport

trait Response {

  def getResponseBody: Option[String]
  def getStatusCode: Int
}

