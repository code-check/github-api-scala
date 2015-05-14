import org.scalatest.FunSpec
import scala.concurrent.Await

class IssueOpSpec extends FunSpec with Constants {

  val number = 1
  val assignee = "shunjikonishi"

  describe("assign operations") {
    it("assign should succeed") {
      val result = Await.result(api.assign(organization, repo, number, assignee), TIMEOUT)
      assert(result.get("assignee.login") == assignee)
    }

    it("Response: assign()") {
      val result = Await.result(api.assign(organization, repo, number, assignee), TIMEOUT)
      if (showResponse) println(result) 
    }

    it("unassign should succeed") {
      val result = Await.result(api.unassign(organization, repo, number), TIMEOUT)
      assert(result.opt("assignee").isEmpty)
    }
  }
}
