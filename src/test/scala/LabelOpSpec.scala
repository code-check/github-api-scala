import org.scalatest.FunSpec
import scala.concurrent.Await
import codecheck.github.models.Label
import codecheck.github.models.LabelInput
import codecheck.github.exceptions.GitHubAPIException
import codecheck.github.exceptions.NotFoundException

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
      assert(label.url.length > 0)
      assert(label.color.length == 6)
    }
    it("replaceLabels should succeed") {
      val result = Await.result(api.replaceLabels(owner, repo, number, "duplicate", "invalid"), TIMEOUT)
      assert(result.length == 2)
      assert(result.filter(_.name == "duplicate").length == 1)
      assert(result.filter(_.name == "invalid").length == 1)      
    }
    it("removeLabel should succeed") {
      val result = Await.result(api.removeLabel(owner, repo, number, "duplicate"), TIMEOUT)
      assert(result.length == 1)

      val result2 = Await.result(api.removeLabel(owner, repo, number, "invalid"), TIMEOUT)
      assert(result2.length == 0)
    }
    it("listLabels should succeed") {
      Await.result(api.addLabels(owner, repo, number, "invalid"), TIMEOUT)
      val result = Await.result(api.listLabels(owner, repo, number), TIMEOUT)
      assert(result.length == 1)
      val label = result.head
      assert(label.name == "invalid")
      assert(label.url.length > 0)
      assert(label.color.length == 6)
    }
  }

  describe("LabelDef operations") {
    it("listLabelDefs should succeed") {
      val result = Await.result(api.listLabelDefs(owner, repo), TIMEOUT)
      assert(result.length > 0)
      val label = result.head
      assert(label.name.length > 0)
      assert(label.url.length > 0)
      assert(label.color.length == 6)
    }
    it("getLabelDef should succeed") {
      val label = Await.result(api.getLabelDef(owner, repo, "question"), TIMEOUT)
      assert(label.name == "question")
      assert(label.url.length > 0)
      assert(label.color == "cc317c")
    }
    it("createLabelDef should succeed") {
      val input = LabelInput("test", "cc317c")
      val label = Await.result(api.createLabelDef(owner, repo, input), TIMEOUT)
      assert(label.name == "test")
      assert(label.url.length > 0)
      assert(label.color == "cc317c")
    }
    it("createLabelDef again should fail") {
      val input = LabelInput("test", "cc317c")
      try {
        val label = Await.result(api.createLabelDef(owner, repo, input), TIMEOUT)
        fail
      } catch {
        case e: GitHubAPIException =>
          assert(e.error.errors.length == 1)
          assert(e.error.errors.head.code == "already_exists")
      }
    }
    it("updateLabelDef should succeed") {
      val input = LabelInput("test", "84b6eb")
      val label = Await.result(api.updateLabelDef(owner, repo, "test", input), TIMEOUT)
      assert(label.name == "test")
      assert(label.url.length > 0)
      assert(label.color == "84b6eb")
    }
    it("removeLabelDef should succeed") {
      val result = Await.result(api.removeLabelDef(owner, repo, "test"), TIMEOUT)
      assert(result)
    }
    it("removeLabelDef again should fail") {
      try {
        val result = Await.result(api.removeLabelDef(owner, repo, "test"), TIMEOUT)
        fail
      } catch {
        case e: NotFoundException =>
          assert(e.error.errors.length == 0)
      }
    }
  }
}
