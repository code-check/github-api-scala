import org.scalatest.path.FunSpec
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import codecheck.github.models.SortDirection
import codecheck.github.models.SearchRepositoryInput
import codecheck.github.models.SearchCodeInput
import codecheck.github.models.SearchIssueInput
import codecheck.github.models.SearchUserInput
import codecheck.github.models.SearchRepositorySort
import codecheck.github.models.SearchCodeSort
import codecheck.github.models.SearchIssueSort
import codecheck.github.models.SearchUserSort
import codecheck.github.models.SearchRepositoryResult
import codecheck.github.models.SearchCodeResult
import codecheck.github.exceptions.GitHubAPIException

class SearchOpSpec extends FunSpec
  with Constants
{

  describe("searchRepositories") {
    it("with valid SearchInput should succeed") {
      val q = "tetris language:assembly".trim.replaceAll(" ","+")
      val input = SearchRepositoryInput(q,sort=Some(SearchRepositorySort.stars),order=SortDirection.desc)
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
      val q = "jquery in:name,description".trim.replaceAll(" ","+")
      val input = SearchRepositoryInput(q,sort=Some(SearchRepositorySort.stars),order=SortDirection.desc)
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
      val q = "addClass in:file language:js repo:jquery/jquery".trim.replaceAll(" ","+")
      val input = SearchCodeInput(q,sort=None,order=SortDirection.desc)
      val res = Await.result(api.searchCode(input), TIMEOUT)
      assert(res.total_count >= 1)
      assert(res.items(0).repository.id >= 1 )
      assert(res.items(0).sha.length >= 40)
      assert(res.items(0).score >= 0d)
      assert(res.items(0).repository.full_name == "jquery/jquery")
    }
    it("with valid SearchInput it should succeed") {
      val q = "function size:10000 language:python".trim.replaceAll(" ","+")
      val input = SearchCodeInput(q,sort=Some(SearchCodeSort.indexed),order=SortDirection.asc)
      val res = Await.result(api.searchCode(input), TIMEOUT)
      assert(res.total_count >= 1)
      assert(res.items(0).repository.id >= 1 )
      assert(res.items(0).path.endsWith(".py"))
      assert(res.items(0).sha.length >= 40)
      assert(res.items(0).score >= 0d)
      assert(res.items(0).repository.`private` == false)
    }
  }
  describe("searchIssues") {
    it("with valid SearchInput should succeed") {
      val q = "windows label:bug language:python state:open".trim.replaceAll(" ","+")
      val input = SearchIssueInput(q,sort=Some(SearchIssueSort.created),order=SortDirection.desc)
      val res = Await.result(api.searchIssues(input), TIMEOUT)
      assert(res.total_count >= 1)
      assert(res.items(0).labels(0).name == "bug" )
      assert(res.items(0).state == "open")
      assert(((res.items(0).created_at).compareTo(res.items(1).created_at)) > 0)
    }
  }
  describe("searchUser") {
    it("with valid SearchInput should succeed") {
      val q = "tom repos:>42 followers:>1000"
        .trim.replaceAll(" ","+")
        .replaceAll(">","%3E")
      val input = SearchUserInput(q,sort=None,order=SortDirection.desc)
      val res = Await.result(api.searchUser(input), TIMEOUT)
      assert(res.total_count >= 0)
      assert(res.items(0).login.length >= 0)
      assert(res.items(0).id >= 0)
    }
  }
}
