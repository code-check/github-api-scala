package codecheck.github.transport

trait CompletionHandler {

  def onCompleted(res: Response): Unit
  def onThrowable(t: Throwable): Unit

}

