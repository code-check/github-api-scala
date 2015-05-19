import org.scalatest.path.FunSpec
import codecheck.github.models.Collaborator
import scala.concurrent.Await

class CollaboratorOpSpec extends FunSpec with Constants {

  describe("listCollaborators"){
    it("should return atleast one Collaborator"){
      val res = Await.result(api.listCollaborators(organization,repo),TIMEOUT)
      assert(res.length >= 1)
      val c = res(0)
      assert(c.login.length > 0)
      assert(c.id > 0)
      assert(c.avatar_url.length > 0)
      assert(c.url.length > 0)
      assert(c.site_admin == false)
    }
  }
}
