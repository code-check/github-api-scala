import org.scalatest.FunSpec
import codecheck.github.api.GitHubAPI

class GitHubRepositorySpec extends FunSpec {
  private val token = "Not yet"
  private val api = GitHubAPI(token)

  describe("GitHubRepository") {
    describe("with dummy repo") {
      val repo = api.repository("dummy", "repo")
      it("should has owner 'dummy'") {
        assert(repo.owner == "dummy")
      }
      it("should has repo 'repo'") {
        assert(repo.repo == "repo")
      }
    }
  }
}
