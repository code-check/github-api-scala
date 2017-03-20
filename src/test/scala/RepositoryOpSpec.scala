package codecheck.github
package operations

import org.scalatest.path.FunSpec
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global

class RepositoryOpSpec extends FunSpec with api.Constants
{

  describe("listOwnRepositories") {
    it("should succeed") {
      val list = Await.result(api.listOwnRepositories(), TIMEOUT)
      assert(list.size > 0)
    }

    it("Response: listOwnRepositories()") {
      val list = Await.result(api.listOwnRepositories(), TIMEOUT)
      showResponse(list)
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
      showResponse(list)
      assert(list.size > 0)
    }
  }
  describe("listOrgRepositories") {
    it("should succeed with valid organization.") {
      val list = Await.result(api.listOrgRepositories(organization), TIMEOUT)
      assert(list.size > 0)
    }

    it("Response: listOrgRepositories()") {
      val list = Await.result(api.listOrgRepositories(organization), TIMEOUT)
      showResponse(list)
      assert(list.size > 0)
    }


  }
  describe("getRepository") {
    it("should succeed") {
      Await.result(api.getRepository(organization, repo), TIMEOUT).map { res =>
        assert(res.owner.login == organization)
        assert(res.name == repo)
        assert(res.full_name == organization + "/" + repo)
        assert(res.url == "https://api.github.com/repos/" + organization + "/" + repo)
        //ToDo add more fields
      }
    }
    it("should be None") {
      assert(Await.result(api.getRepository(organization, repoInvalid), TIMEOUT).isEmpty)
    }
  }

  describe("listLanguages") {
    it("should succeed") {
      val username = "shunjikonishi"
      val reponame = "programming-game"
      val list = Await.result(api.listLanguages(username, reponame), TIMEOUT)
      assert(list.items.size > 0)
      val sumRate = list.items.map(_.rate).sum
      assert(sumRate > 0.99 && sumRate <= 1.0)
    }
  }

/*
  describe("createUserRepository") {
    val createRepoName = "create-repo-name"
    it("should succeed") {
      val input = RepositoryInput(name = createRepoName)
      val repo = Await.result(api.createUserRepository(input), TIMEOUT)
      assert(repo.owner.login == user)
      assert(repo.name == "create-repo-test")
    }
    it("should fail with existing repository name") {
      val input = RepositoryInput(name = createRepoName)
      try {
        val repo = Await.result(api.createUserRepository(input), TIMEOUT)
        fail
      } catch {
        case e: ApiException =>
          assert(e.error.errors.head.field == "name")
        case e: Throwable =>
          fail
      }
    }
  }
*/
}
