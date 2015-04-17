
import com.ning.http.client.AsyncHttpClient
import codecheck.github.api.GitHubAPI
import scala.concurrent.duration._

trait Constants {

  protected val TIMEOUT = 5 seconds
  protected val api = Constants.API
}

object Constants {
  private val token = sys.env("GITHUB_TOKEN")
  implicit val client = new AsyncHttpClient()

  val API = GitHubAPI(token)
}