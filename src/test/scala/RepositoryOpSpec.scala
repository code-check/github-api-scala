import org.scalatest.path.FunSpec
import codecheck.github.exceptions.NotFoundException
import codecheck.github.models.Repository
import codecheck.github.exceptions.GitHubAPIException
import codecheck.github.exceptions.NotFoundException
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global

class RepositoryOpSpec extends FunSpec 
  with Constants 
{

  describe("listOwnRepositories") {
    it("should succeed") {
      val list = Await.result(api.listOwnRepositories(), TIMEOUT)
      assert(list.size > 0)
    }

    it("Response: listOwnRepositories()") {
      val list = Await.result(api.listOwnRepositories(), TIMEOUT)
      if (showResponse) println(list) 
    }
    //ToDo option test
  }
  describe("listUserRepositories") {
    it("should succeed") {
      val list = Await.result(api.listUserRepositories(otherUser), TIMEOUT)
      assert(list.size > 0)
    }

    it("Response: listUserRepositories()") {
      val list = Await.result(api.listUserRepositories(otherUser), TIMEOUT)
      assert(list.size > 0)
      if (showResponse) println(list) 
    }
  }
  describe("listOrgRepositories") {
    it("should succeed") {
      val list = Await.result(api.listOrgRepositories(organization), TIMEOUT)
      assert(list.size > 0)
    }

    it("Response: listOrgRepositories()") {
      val list = Await.result(api.listOrgRepositories(organization), TIMEOUT)
      assert(list.size > 0)
      if (showResponse) println(list) 
    }
  }
  describe("getRepository") {
    it("should succeed") {
      Await.result(api.getRepository(organization, repo), TIMEOUT).map { res =>
        assert(res.name == repo)
        assert(res.owner.login == organization)
        //ToDo other field
      }
    }
    it("should be None") {
      assert(Await.result(api.getRepository(organization, repoInvalid), TIMEOUT).isEmpty)
    }
  }
}
