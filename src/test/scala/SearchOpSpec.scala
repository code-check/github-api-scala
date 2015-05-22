import org.scalatest.path.FunSpec
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import codecheck.github.models.SortDirection
import codecheck.github.models.SearchInput
import codecheck.github.models.SearchSort
import codecheck.github.models.SearchRepositoryResult

class SearchOpSpec extends FunSpec
  with Constants
{

  describe("searchRepositories") {
    it("with valid SearchInput should succeed") {
      var q = "tetris language:assembly"
      val q1 = q.trim.replaceAll(" ","+");
      val input = SearchInput(q1,sort=Some(SearchSort.stars),order=SortDirection.desc)
      Await.result(api.searchRepositories(input), TIMEOUT).map { res =>
        assert(res.total_count >= 1)
        assert(res.items(0).id >= 1 )
        assert(res.items(0).name.length >= 1)
        assert(res.items(0).full_name.length >= 1)
        assert(res.items(0).description.isDefined)
        assert(res.items(0).open_issues_count >= 0)
        println("RESULT" + res)
      }
    }
    it("with valid changed query(q) SearchInput should succeed") {
      var q = "jquery in:name,description"
      val q1 = q.trim.replaceAll(" ","+");
      val input = SearchInput(q1,sort=Some(SearchSort.stars),order=SortDirection.desc)
      Await.result(api.searchRepositories(input), TIMEOUT).map { res =>
        assert(res.total_count >= 1)
        assert(res.items(0).id >= 1 )
        assert(res.items(0).name.length >= 1)
        assert(res.items(0).full_name.length >= 1)
        assert(res.items(0).description.isDefined)
        assert(res.items(0).open_issues_count >= 0)
        println("RESULT" + res)
      }
    }
  }
}
