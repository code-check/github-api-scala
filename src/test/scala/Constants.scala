
import com.ning.http.client.AsyncHttpClient
import codecheck.github.api.GitHubAPI
import scala.concurrent.duration._
import org.scalatest.time.Span._
import org.scalatest.concurrent.ScalaFutures

trait Constants {
  import scala.language.postfixOps

  protected val TIMEOUT = 5 seconds
  protected val api = Constants.API

  protected val owner = "code-check"
  protected val repo = "test-repo"
}

object Constants {
  private val token = sys.env("GITHUB_TOKEN")
  implicit val client = new AsyncHttpClient()

  val API = GitHubAPI(token)
}