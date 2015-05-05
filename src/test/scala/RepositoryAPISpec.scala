import org.scalatest.FunSpec

class RepositoryAPISpec extends FunSpec with Constants {

  describe("with dummy repo") {
    val repo = api.repositoryAPI("dummy", "repo")
    it("should has owner 'dummy'") {
      assert(repo.owner == "dummy")
    }
    it("should has repo 'repo'") {
      assert(repo.repo == "repo")
    }
  }

  
}
