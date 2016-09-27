package codecheck.github.transport

trait Request {
  def setBody(body: String): Request
  def setHeader(name: String, value: String): Request

  def execute(handler: CompletionHandler): Unit
}

