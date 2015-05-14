
import com.ning.http.client.AsyncHttpClient
import codecheck.github.api.GitHubAPI
import scala.concurrent.duration._
import org.scalatest.time.Span._
import org.scalatest.concurrent.ScalaFutures

trait Constants {
  import scala.language.postfixOps

  protected val TIMEOUT = 5 seconds
  protected val api = Constants.API

  protected val user = "fanwashere" //REQUIRED: Edit this to your own username.

  //Request membership of dummy organization "celestialbeing" if you are not member. Do not edit.
  protected val organization = "celestialbeings"
  protected val repo = "test-repo"

  //Other Options
  protected val showResponse = true //Set true to see all response outputs
  protected val otherUser = "shunjikonishi" 
  protected val otherUserInvalid = "loremipsom123"
  protected val repoInvalid = "loremipsom123"

}

object Constants {
  private val token = sys.env("GITHUB_TOKEN")
  implicit val client = new AsyncHttpClient()

  val API = GitHubAPI(token)
}