import org.scalatest.path.FunSpec
import codecheck.github.exceptions.NotFoundException
import codecheck.github.models.Repository
import codecheck.github.exceptions.GitHubAPIException
import codecheck.github.exceptions.NotFoundException
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global

class UserOpSpec extends FunSpec 
  with Constants 
{

  describe("getUser") {
    it("with valid username should succeed") {
      val userOp = Await.result(api.getUser("shunjikonishi"), TIMEOUT)
      assert(userOp.isDefined)
      val user = userOp.get
      assert(user.login == "shunjikonishi")
    }
    it("with invalid username should be None") {
      val userOp = Await.result(api.getUser("shunjikonishi-x"), TIMEOUT)
      assert(userOp.isEmpty)
    }
  }
}
