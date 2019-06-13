package codecheck.github.transport.asynchttp20

import org.asynchttpclient.{AsyncHttpClient, Response => AsyncHttpResponse, AsyncCompletionHandler, BoundRequestBuilder}

import codecheck.github.transport.{Transport, Request, Response, CompletionHandler}

class AsyncHttp20Transport(client: AsyncHttpClient) extends Transport{

  def prepareGet(url: String): Request    = new AsyncHttp20Request(client.prepareGet(url))
  def preparePost(url: String): Request   = new AsyncHttp20Request(client.preparePost(url))
  def preparePut(url: String): Request    = new AsyncHttp20Request(client.preparePut(url))
  def prepareDelete(url: String): Request = new AsyncHttp20Request(client.prepareDelete(url))

  def close: Unit = client.close()

}

class AsyncHttp20Request(request: BoundRequestBuilder) extends Request {

  def setBody(body: String): Request = {
    request.setBody(body)
    this
  }

  def setHeader(name: String, value: String): Request = {
    request.setHeader(name, value)
    this
  }

  def setFollowRedirect(b: Boolean): Request = {
    request.setFollowRedirect(b)
    this
  }

  def addFormParam(name: String, value: String): Request = {
    request.addFormParam(name, value)
    this
  }

  def execute(handler: CompletionHandler): Unit = {
    request.execute(new AsyncCompletionHandler[AsyncHttpResponse]() {
      def onCompleted(res: AsyncHttpResponse) = {
        handler.onCompleted(new AsyncHttp20Response(res))
        res
      }
      override def onThrowable(t: Throwable): Unit = {
        handler.onThrowable(t)
        super.onThrowable(t)
      }
    })
  }
}

class AsyncHttp20Response(response: AsyncHttpResponse) extends Response {

  def getResponseBody: Option[String] = Option(response.getResponseBody())
  def getStatusCode: Int = response.getStatusCode
}


