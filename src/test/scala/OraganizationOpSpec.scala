import org.scalatest.FunSpec
import scala.concurrent.Await

class OrganizationOpSpec extends FunSpec with Constants {

  describe("listOrganizations(user)") {
    it("should return multiple organizations.") {
      val result = Await.result(api.listOrganizations("shunjikonishi"), TIMEOUT)
      assert(result.length > 1)
    }
  }
  describe("listOrganizations") {
    it("should return multiple organizations.") {
      val result = Await.result(api.listOrganizations, TIMEOUT)
      assert(result.length > 1)
    }
  }
  describe("getOrganization") {
    it("should return correct values.") {
      val org = Await.result(api.getOrganization("code-check"), TIMEOUT)
      println(org)
      assert(org.login == "code-check")
      assert(org.id > 0)
      assert(org.url == "https://api.github.com/orgs/code-check")
      assert(org.avatar_url.length > 0)
      assert(org.description == "")
      assert(org.name == "Code-Check")
      assert(org.company.isEmpty)
      assert(org.email == "")
      assert(org.public_repos > 0)
      assert(org.public_gists == 0)
    }
  }
}
