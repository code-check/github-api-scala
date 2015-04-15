import codecheck.github.api.GitHubAPI
import scala.concurrent.duration._

trait Constants {
  private val token = sys.env("GITHUB_TOKEN")

  protected val TIMEOUT = 5 seconds
  protected val API = GitHubAPI(token)
}