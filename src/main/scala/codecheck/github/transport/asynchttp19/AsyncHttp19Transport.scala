package codecheck.github.transport.asynchttp19

import com.ning.http.client.{AsyncHttpClient, Response => AsyncHttpResponse, AsyncCompletionHandler}

import codecheck.github.transport.{Transport, Request, Response, CompletionHandler}

class AsyncHttp19Transport(client: AsyncHttpClient) extends Transport{

  def prepareGet(url: String): Request    = new AsyncHttp19Request(client.prepareGet(url))
  def preparePost(url: String): Request   = new AsyncHttp19Request(client.preparePost(url))
  def preparePut(url: String): Request    = new AsyncHttp19Request(client.preparePut(url))
  def prepareDelete(url: String): Request = new AsyncHttp19Request(client.prepareDelete(url))

  def close: Unit = client.close()

}

class AsyncHttp19Request(request: AsyncHttpClient#BoundRequestBuilder) extends Request {

  def setBody(body: String): Request = {
    request.setBody(body)
    this
  }

  def setHeader(name: String, value: String): Request = {
    request.setHeader(name, value)
    this
  }

  def setFollowRedirect(b: Boolean): Request = {
    request.setFollowRedirects(b)
    this
  }

  def addFormParam(name: String, value: String): Request = {
    request.addFormParam(name, value)
    this
  }

  def execute(handler: CompletionHandler): Unit = {
    request.execute(new AsyncCompletionHandler[AsyncHttpResponse]() {
      def onCompleted(res: AsyncHttpResponse) = {
        handler.onCompleted(new AsyncHttp19Response(res))
        res
      }
      override def onThrowable(t: Throwable): Unit = {
        handler.onThrowable(t)
        super.onThrowable(t)
      }
    })
  }
}

class AsyncHttp19Response(response: AsyncHttpResponse) extends Response {

  def getResponseBody: Option[String] = Option(response.getResponseBody())
  def getStatusCode: Int = response.getStatusCode
}


