import org.scalatest.path.FunSpec
import codecheck.github.models.UserInput
import scala.concurrent.Await

class CollaboratorsOpSpec extends FunSpec with Constants {

  describe("listCollaborators") {
    it("listCollaborators should succeed") {
      val result = Await.result(api.listCollaborators(organization, repo), TIMEOUT)
      assert(result.length > 1)
    }
  }
}
