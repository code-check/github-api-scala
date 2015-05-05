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
      println(list)
    }
    //ToDo option test
  }
  describe("listUserRepositories") {
    it("should succeed") {
      val list = Await.result(api.listUserRepositories("shunjikonishi"), TIMEOUT)
      assert(list.size > 0)
      println(list)
    }
  }
  describe("listOrgRepositories") {
    it("should succeed") {
      val list = Await.result(api.listOrgRepositories("code-check"), TIMEOUT)
      assert(list.size > 0)
      println(list)
    }
  }
  describe("getRepository") {
    it("should succeed") {
      val repo = Await.result(api.getRepository("code-check", "github-api-scala"), TIMEOUT)
      assert(repo.name == "github-api-scala")
      assert(repo.owner.login == "code-check")
      //ToDo other field
    }
  }
}
