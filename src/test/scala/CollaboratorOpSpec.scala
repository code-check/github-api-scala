import org.scalatest.path.FunSpec
import scala.concurrent.Await
import codecheck.github.models.Collaborator
import codecheck.github.exceptions.GitHubAPIException
import codecheck.github.exceptions.NotFoundException

class CollaboratorOpSpec extends FunSpec with Constants {

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
  describe("isCollaborator"){
    it("if it is Collaborator"){
      val res = Await.result(api.isCollaborator(user, userRepo, collaboratorUser),TIMEOUT)
      assert(res)
    }
    it("if it is not a valid Collaborator"){
      val res1 = Await.result(api.isCollaborator(user, userRepo, otherUserInvalid),TIMEOUT)
      assert(res1 == false)
    }
  }
  describe("listCollaborators"){
    it("should return at least one Collaborator"){
      val res = Await.result(api.listCollaborators(user, userRepo),TIMEOUT)
      val c = res.find(_.login == collaboratorUser)
      assert(c.isDefined)
      assert(c.get.id > 0)
      assert(c.get.avatar_url.length > 0)
      assert(c.get.url.length > 0)
      assert(c.get.site_admin == false)
    }
  }
 describe("removeCollaborator"){
    it("should remove the Collaborator"){
      var res = Await.result(api.removeCollaborator(user, userRepo, collaboratorUser),TIMEOUT)
      assert(res == true)
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
