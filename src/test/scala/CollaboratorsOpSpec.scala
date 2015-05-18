import org.scalatest.path.FunSpec
//import codecheck.github.models.Collaborators
import scala.concurrent.Await

class CollaboratorsOpSpec extends FunSpec with Constants {

  describe("listCollaborators") {
    it("should return atleast one collaborator") {
      val result = Await.result(api.listCollaborators(organization, repo), TIMEOUT)
      assert(result.length >= 1)
    }
    it("should return more then one collaborator") {
      val result = Await.result(api.listCollaborators(organization, repo), TIMEOUT)
      assert(result.length > 1)
    }
  }
}