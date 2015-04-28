package codecheck.github.app

import codecheck.github.api.GitHubAPI
import com.ning.http.client.AsyncHttpClient
import org.json4s._

trait Command {
  val api: GitHubAPI
  implicit val formats: Formats = DefaultFormats
  implicit val client = new AsyncHttpClient
  def done = client.close
}