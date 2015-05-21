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
  val input = SearchInput("tetris",sort=Some(SearchSort.stars),order=SortDirection.desc)
   describe("searchRepositories") {
    it("with valid SearchInput should succeed") {
      Await.result(api.searchRepositories(input), TIMEOUT).map { res =>
        assert(res.total_count >= 1)
        println("RESULT" + res)
      }
    }
  }
}
