import org.scalatest.path.FunSpec
import scala.concurrent.Await
import codecheck.github.models.Collaborator
import codecheck.github.exceptions.GitHubAPIException
import codecheck.github.exceptions.NotFoundException

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
  describe("isCollaborator"){
    it("if it is Collaborator"){
      val res = Await.result(api.isCollaborator(organization, repo, user),TIMEOUT)
      assert(res == true)
    }
    it("if it is not a Collaborator"){
      val res1 = Await.result(api.isCollaborator(organization, repo, otherUserInvalid).failed,TIMEOUT)
      res1 match {
        case e: NotFoundException =>
        case _ => fail
      }
    }
  }
  describe("addCollaborator"){
    it("should add Collaborator User to user Repo"){
      val res = Await.result(api.addCollaborator(user, userRepo, collaboratorUser),TIMEOUT)
      assert(res)
    }
    it("should fail for non existent User Repo"){
      val res = Await.result(api.addCollaborator(user, repoInvalid, collaboratorUser).failed,TIMEOUT)
      res match {
        case e: NotFoundException  =>
        case _ => fail
      }
    }
  }
 describe("removeCollaborator"){
    it("should remove the Collaborator"){
      var res = Await.result(api.removeCollaborator(user, userRepo, collaboratorUser),TIMEOUT)
      assert(res)
    }
  }
  it("should fail for non existent User Repo"){
    var res = Await.result(api.removeCollaborator(user, repoInvalid, collaboratorUser).failed,TIMEOUT)
    res match {
      case e: NotFoundException  =>
      case _ => fail
    }
  }
}
