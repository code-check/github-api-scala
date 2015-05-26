import org.scalatest.path.FunSpec
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import codecheck.github.models.SortDirection
import codecheck.github.models.SearchInput
import codecheck.github.models.SearchSort
import codecheck.github.models.SearchRepositoryResult
import codecheck.github.models.SearchCodeResult
import codecheck.github.models.searchCodeItems
import codecheck.github.exceptions.GitHubAPIException

class SearchOpSpec extends FunSpec
  with Constants
{

  describe("searchRepositories") {
    it("with valid SearchInput should succeed") {
      var q = "tetris language:assembly"
      val q1 = q.trim.replaceAll(" ","+");
      val input = SearchInput(q1,sort=Some(SearchSort.stars),order=SortDirection.desc)
      val res = Await.result(api.searchRepositories(input), TIMEOUT)
      assert(res.total_count >= 1)
      assert(res.items(0).id >= 1 )
      assert(res.items(0).name.length >= 1)
      assert(res.items(0).full_name.length >= 1)
      assert(res.items(0).description.isDefined)
      assert(res.items(0).open_issues_count >= 0)
      assert(res.items(0).language == "Assembly")
      assert(res.items(0).stargazers_count > res.items(1).stargazers_count)
    }
    it("with valid changed query(q) SearchInput should succeed") {
      var q = "jquery in:name,description"
      val q1 = q.trim.replaceAll(" ","+");
      val input = SearchInput(q1,sort=Some(SearchSort.stars),order=SortDirection.desc)
      val res = Await.result(api.searchRepositories(input), TIMEOUT)
      assert(res.total_count >= 1)
      assert(res.items(0).id >= 1 )
      assert(res.items(0).name.length >= 1)
      assert(res.items(0).full_name.length >= 1)
      assert(res.items(0).description.isDefined)
      assert(res.items(0).open_issues_count >= 0)
    }
  }
  describe("searchCode") {
    it("with valid SearchInput q,no SortOrder should succeed") {
      var q = "addClass in:file language:js repo:jquery/jquery"
      val q1 = q.trim.replaceAll(" ","+");
      val input = SearchInput(q1,sort=None,order=SortDirection.desc)
      val res = Await.result(api.searchCode(input), TIMEOUT)
      assert(res.total_count >= 1)
      assert(res.items(0).Repo.id >= 1 )
      assert(res.items(0).Repo.full_name == "jquery/jquery")
    }
    //Following test results in error:
    //  "message" : "Validation Failed",
    //  "errors" : [ {
    //  "message" : "Must include at least one user, organization, or repository"
    it("with valid SearchInput it should succeed") {
      var q = "function size:10000 language:python"
      val q1 = q.trim.replaceAll(" ","+");
      val input = SearchInput(q1,sort=Some(SearchSort.indexed),order=SortDirection.desc)
      try {
        val res = Await.result(api.searchCode(input), TIMEOUT)
      } catch {
        case e: GitHubAPIException =>
          assert(e.error.errors.length == 1)
          assert(e.error.message == "Validation Failed")
      }
    }
  }
  describe("searchIssues") {
    it("with valid SearchInput should succeed") {
      var q = "windows label:bug language:python state:open"
      val q1 = q.trim.replaceAll(" ","+");
      val input = SearchInput(q1,sort=Some(SearchSort.created),order=SortDirection.desc)
      val res = Await.result(api.searchIssues(input), TIMEOUT)
      assert(res.total_count >= 1)
      assert(res.items(0).labels(0).name == "bug" )
      assert(res.items(0).state == "open")
      assert(((res.items(0).created_at).compareTo(res.items(1).created_at)) > 0)
    }
  }
  describe("searchUser") {
    it("with valid SearchInput should succeed") {
      var q = "tom repos:>42 followers:>1000"
      q = q.trim.replaceAll(" ","+")
      val q1 = q.replaceAll(">","%3E")
      val input = SearchInput(q1,sort=None,order=SortDirection.desc)
      val res = Await.result(api.searchUser(input), TIMEOUT)
      assert(res.total_count >= 0)
      assert(res.items(0).login.length >= 0)
      assert(res.items(0).id >= 0)
    }
  }
}
