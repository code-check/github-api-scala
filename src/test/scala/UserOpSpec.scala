import org.scalatest.path.FunSpec
import codecheck.github.exceptions.NotFoundException
import codecheck.github.models.Repository
import codecheck.github.exceptions.GitHubAPIException
import codecheck.github.exceptions.NotFoundException
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import codecheck.github.models.UserInput

class UserOpSpec extends FunSpec 
  with Constants 
{
  describe("getUser") {
    it("with valid username should succeed") {
      val userOp = Await.result(api.getUser("sukeshni"), TIMEOUT)
      assert(userOp.isDefined)
      val user = userOp.get
      assert(user.login == "sukeshni")
    }
    it("with invalid username should be None") {
      val userOp = Await.result(api.getUser("sukeshni-wrong"), TIMEOUT)
      assert(userOp.isEmpty)
    }
  }

  describe("updateAuthenticatedUser") {
    it("if values updated correctly should succeed") {
      val input = new UserInput(Some("firstname lastname"))
      val res = Await.result(api.updateAuthenticatedUser(input), TIMEOUT)
      assert(res.name == "firstname lastname")
    }
  }
}