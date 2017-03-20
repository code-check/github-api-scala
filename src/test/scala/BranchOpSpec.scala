package codecheck.github
package operations

import org.scalatest.FunSpec
import org.scalatest.BeforeAndAfterAll
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global

class BranchOpSpec extends FunSpec
  with api.Constants
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
