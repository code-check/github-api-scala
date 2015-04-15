import org.scalatest.FunSpec

class GitHubRepositorySpec extends FunSpec with Constants {

  describe("with dummy repo") {
    val repo = API.repository("dummy", "repo")
    it("should has owner 'dummy'") {
      assert(repo.owner == "dummy")
    }
    it("should has repo 'repo'") {
      assert(repo.repo == "repo")
    }
  }

  
}
