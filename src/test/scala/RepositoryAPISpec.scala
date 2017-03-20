package codecheck.github
package api

import org.scalatest.FunSpec

class RepositoryAPISpec extends FunSpec with api.Constants {
	
  val gDummy = generateRandomString
  val gRepo = generateRandomString

  describe("with dummy repo") {
    val repo = api.repositoryAPI(gDummy, gRepo)
    it(s"should has owner ${gDummy}") {
      assert(repo.owner == gDummy)
    }
    it(s"should has repo ${gRepo}") {
      assert(repo.repo == gRepo)
    }
  }
}
