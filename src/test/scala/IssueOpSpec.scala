import org.scalatest.FunSpec
import scala.concurrent.Await

class IssueOpSpec extends FunSpec with Constants {

  val number = 1
  val assignee = "shunjikonishi"

  describe("assign operations") {
    it("assign should succeed") {
      val result = Await.result(api.assign(owner, repo, number, assignee), TIMEOUT)
      assert(result.get("assignee.login") == assignee)
      println(result)
    }
    it("unassign should succeed") {
      val result = Await.result(api.unassign(owner, repo, number), TIMEOUT)
      assert(result.opt("assignee").isEmpty)
    }
  }
}
