
import com.ning.http.client.AsyncHttpClient
import codecheck.github.api.GitHubAPI
import scala.concurrent.duration._
import org.scalatest.time.Span._
import org.scalatest.concurrent.ScalaFutures

trait Constants {

  protected val TIMEOUT = 5 seconds
  protected val api = Constants.API

  protected implicit def duration2timeout(d: Duration) = ScalaFutures.timeout(d)
}

object Constants {
  private val token = sys.env("GITHUB_TOKEN")
  implicit val client = new AsyncHttpClient()

  val API = GitHubAPI(token)
}