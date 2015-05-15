import org.scalatest.FunSpec
import scala.concurrent.Await

class IssueOpSpec extends FunSpec with Constants {

  val number = 1

  describe("assign operations") {
    it("assign should succeed") {
      val result = Await.result(api.assign(organization, repo, number, user), TIMEOUT)
      showResponse(result)
      assert(result.get("assignee.login") == user)
    }

    it("unassign should succeed") {
      val result = Await.result(api.unassign(organization, repo, number), TIMEOUT)
      assert(result.opt("assignee").isEmpty)
    }
  }
}
