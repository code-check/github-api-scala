import org.scalatest.FunSpec
import scala.concurrent.Await

import codecheck.github.models.OrganizationInput

class OrganizationOpSpec extends FunSpec with Constants {

  describe("listOwnOrganizations(user)") {
    it("should return multiple organizations.") {
      val result = Await.result(api.listOwnOrganizations, TIMEOUT)
      assert(result.length > 1)
    }
  }
  describe("listUserOrganizations") {
    it("should return multiple organizations.") {
      val result = Await.result(api.listUserOrganizations("shunjikonishi"), TIMEOUT)
      assert(result.length > 1)
    }
  }
  describe("getOrganization") {
    it("should return correct values.") {
      Await.result(api.getOrganization("code-check"), TIMEOUT).map { org =>
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
    it("should be None.") {
      assert(Await.result(api.getOrganization("code-check-x"), TIMEOUT).isEmpty)
    }
  }
  describe("updateOrganization") {
    it("should return true if values updated correctly") {
      val input = new OrganizationInput(Some("Celestial Beings"), None, None, Some("Moon"), None, None)
      Await.result(api.updateOrganization("celestialbeings", input), TIMEOUT).map { org =>
        assert(org.name == "Celestial Beings")
        assert(org.location == "Moon")
      }
    }
  }
}
