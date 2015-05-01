package codecheck.github.app

import codecheck.github.api.GitHubAPI
import com.ning.http.client.AsyncHttpClient
import org.json4s._
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global

trait Command {
  implicit val formats = codecheck.github.utils.Json4s.formats

  val api: GitHubAPI
}