import org.scalatest.FunSpec
import scala.concurrent.Await

class LabelOpSpec extends FunSpec with Constants {

  val owner = "code-check"
  val repo = "github-api-scala"
  val number = 1

  describe("Label operations") {
    it("removeAllLabels should succeed") {
      val result = Await.result(api.removeAllLabels(owner, repo, number), TIMEOUT)
      assert(result.length == 0)
    }
    it("addLabel should succeed") {
      val result = Await.result(api.addLabels(owner, repo, number, "bug"), TIMEOUT)
      assert(result.length == 1)
      val label = result.head
      assert(label.name == "bug")
      assert(label.url.isDefined)
      assert(label.color.length == 6)
    }
    it("replaceLabel should succeed") {
      val result = Await.result(api.replaceLabels(owner, repo, number, "duplicate", "invalid"), TIMEOUT)
      assert(result.length == 2)
      assert(result.filter(_.name == "duplicate").length == 1)
      assert(result.filter(_.name == "invalid").length == 1)      
    }
    it("removeLabel should succeed") {
      val result = Await.result(api.removeLabels(owner, repo, number, "duplicate"), TIMEOUT)
      assert(result.length == 1)

      val result2 = Await.result(api.removeLabels(owner, repo, number, "invalid"), TIMEOUT)
      assert(result2.length == 0)
    }
    it("listLabels should succeed") {
      Await.result(api.addLabels(owner, repo, number, "invalid"), TIMEOUT)
      val result = Await.result(api.listLabels(owner, repo, number), TIMEOUT)
      assert(result.length == 1)
      val label = result.head
      assert(label.name == "invalid")
      assert(label.url.isDefined)
      assert(label.color.length == 6)
    }
  }
}
