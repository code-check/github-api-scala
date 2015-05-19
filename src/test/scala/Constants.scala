
import com.ning.http.client.AsyncHttpClient
import codecheck.github.api.GitHubAPI
import scala.concurrent.duration._
import scala.util.Random._
import org.scalatest.time.Span._
import org.scalatest.concurrent.ScalaFutures

trait Constants {
  import scala.language.postfixOps

  protected val TIMEOUT = 5 seconds
  protected val api = Constants.API

  protected val user = "sukeshni" //REQUIRED: Edit this to your own username.

  protected val userRepo = "test-repo" //REQUIRED: Edit this to your repo
  protected val collaboratorUser = "givery-dev"

  //Request membership of dummy organization "celestialbeing" if you are not member. Do not edit.
  protected val organization = "celestialbeings"
  protected val repo = "test-repo"

  //Other Options
  private val debug = sys.env.get("DEBUG")
    .map(v => v.equalsIgnoreCase("true") || v.equalsIgnoreCase("yes")).getOrElse(false)

  protected def showResponse(v: Any): Unit = {
    if (debug) {
      println(v)
    }
  }
  protected val otherUser = "shunjikonishi"
  protected val otherUserInvalid = "loremipsom123"
  protected val organizationInvalid = "loremipsom123"
  protected val repoInvalid = "loremipsom123"

  val wordBank = Array("Alpha", "Beta", "Gamma", "Delta", "Epsilon", "Zeta", "Theta", "Lambda", "Pi", "Sigma")
  def generateRandomString: String =
  wordBank(nextInt(10)) + " " + wordBank(nextInt(10)) + " " + wordBank(nextInt(10))
  def generateRandomWord: String = wordBank(nextInt(10))
  def generateRandomInt: Int = nextInt(1000)
}

object Constants {
  private val token = sys.env("GITHUB_TOKEN")
  implicit val client = new AsyncHttpClient()

  val API = GitHubAPI(token)
}
