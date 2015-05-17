import org.scalatest.FunSpec
import org.scalatest.BeforeAndAfterAll
import codecheck.github.exceptions.NotFoundException
import codecheck.github.models.Repository
import codecheck.github.exceptions.GitHubAPIException
import codecheck.github.exceptions.NotFoundException
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import codecheck.github.models.UserInput

class UserOpSpec extends FunSpec 
  with Constants 
  with BeforeAndAfterAll
{
  val origin = Await.result(api.getAuthenticatedUser, TIMEOUT)

  override def afterAll() {
    val input = UserInput(
      Some(origin.name),
      Some(origin.email),
      Some(origin.blog),
      Some(origin.company),
      Some(origin.location),
      Some(origin.hireable),
      origin.bio
    )
    val user = Await.result(api.updateAuthenticatedUser(input), TIMEOUT)
    println("AFTER: " + user)
  }
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
      val input = new UserInput(
        Some("firstname lastname"),
        Some("test@givery.co.jp"),
        Some("Blog"),
        Some("Anywhere"),
        Some("Somewhere"),
        Some(!origin.hireable),
        Some("bio")
      )
      val res = Await.result(api.updateAuthenticatedUser(input), TIMEOUT)
      println("TEST: " + res)
      assert(res.name == input.name.get)
      assert(res.email == input.email.get)
      assert(res.blog == input.blog.get)
      assert(res.company == input.company.get)
      assert(res.location == input.location.get)
      assert(res.bio.get == input.bio.get)
    }
  }

  describe("getAllUsers") {
    it("with no since parameter it should succeed") {
      val res = Await.result(api.getAllUsers(), TIMEOUT)
      println("TEST: " + res)
      assert(res{0}.id == 1)
    }
    it("with valid since parameter it should succeed") {
      val userOp = Await.result(api.getUser("sukeshni"), TIMEOUT)//give a valid username
      assert(userOp.isDefined)
      val userOpGet = userOp.get
      val res = Await.result(api.getAllUsers(userOpGet.id), TIMEOUT)
      println("TEST: " + res)
      assert((res{0}.id).toLong == userOpGet.id+1)
    }
  }
}