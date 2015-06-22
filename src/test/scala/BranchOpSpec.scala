import org.scalatest.FunSpec
import org.scalatest.BeforeAndAfterAll
import codecheck.github.exceptions.NotFoundException
import codecheck.github.models._
import codecheck.github.exceptions.GitHubAPIException
import codecheck.github.exceptions.NotFoundException
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import codecheck.github.models.UserInput

class BranchOpSpec extends FunSpec
  with Constants
  with BeforeAndAfterAll
{
  describe("getBranch") {
    it("with valid repo and branch should succeed") {
      val branchOp = Await.result(api.getBranch(user, userRepo, "master"), TIMEOUT)
      assert(branchOp.isDefined)
      assert(branchOp.get.name == "master")
    }
    it("with invalid branch should be None") {
      val branchOp = Await.result(api.getBranch(user, userRepo, "unknown"), TIMEOUT)
      assert(branchOp.isEmpty)
    }
  }

  describe("listBranches") {
    it("with valid repo should succeed") {
      val list = Await.result(api.listBranches(user, userRepo), TIMEOUT)
      assert(list.length > 0)
      assert(list.exists(_.name == "master"))
    }
  }
}
