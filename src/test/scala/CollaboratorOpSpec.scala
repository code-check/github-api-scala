import org.scalatest.path.FunSpec
import codecheck.github.models.Collaborator
import scala.concurrent.Await

class CollaboratorOpSpec extends FunSpec with Constants {

  describe("listCollaborators") {
    it("should return atleast one collaborator") {
      val res = Await.result(api.listCollaborators(organization, repo), TIMEOUT)
      assert(res.length >= 1)
    }
  }
}