package codecheck.github.app

import codecheck.github.api.GitHubAPI
import com.ning.http.client.AsyncHttpClient
import org.json4s._
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global

trait Command {
  val api: GitHubAPI
  implicit val formats: Formats = DefaultFormats
  implicit val client = new AsyncHttpClient
  def done: Unit = client.close
  def done[T](ret: Seq[Future[T]]): Unit = {
    Await.result(Future.sequence(ret), Duration.Inf)
    done
  }
}