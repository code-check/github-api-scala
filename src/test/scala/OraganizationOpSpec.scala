import org.scalatest.FunSpec
import org.scalatest.BeforeAndAfter
import scala.concurrent.Await
import org.joda.time.DateTime
import org.joda.time.DateTimeZone

import codecheck.github.models.OrganizationInput

class OrganizationOpSpec extends FunSpec with Constants with BeforeAndAfter {

  val gName = Some(generateRandomString)
  val gCompany = Some(generateRandomString)
  val gDescription = Some(generateRandomString)
  val gLocation = Some(generateRandomString)

  before {

  }

  after {
    val input = new OrganizationInput(
      Some("celestialbeings"),
      Some("givery"),
      Some("No description"),
      Some("Tokyo")
    )
    Await.result(api.updateOrganization(organization, input), TIMEOUT)
  }

  describe("listOwnOrganizations") {
    it("should return at least one organization.") {
      val result = Await.result(api.listOwnOrganizations, TIMEOUT)
      assert(result.length >= 1)
    }

    it("should return multiple organizations if user belongs in more than one.") {
      val result = Await.result(api.listOwnOrganizations, TIMEOUT)
      assert(result.length > 1)
    }
  }

  describe("listUserOrganizations(user)") {
    it("should return at least one organization.") {
      val result = Await.result(api.listUserOrganizations(otherUser), TIMEOUT)
      assert(result.length >= 1)
    }

    it("should return multiple organizations if user belongs in more than one.") {
      val result = Await.result(api.listUserOrganizations(otherUser), TIMEOUT)
      assert(result.length > 1)
    }
  }

  describe("updateOrganization") {
    it("should return true if values updated correctly") {
      val input = new OrganizationInput(gName, gCompany, gDescription, gLocation)
      val org = Await.result(api.updateOrganization(organization, input), TIMEOUT)
      assert(org.name == gName.get)
      assert(org.company == gCompany)
      assert(org.description == gDescription.get)
      assert(org.location == gLocation.get)
    }
  }

  describe("getOrganization") {
    it("should return correct values.") {
      Await.result(api.getOrganization(organization), TIMEOUT).map { org =>
        showResponse(org)
        assert(org.login == "celestialbeings")
        assert(org.id > 0)
        assert(org.url == "https://api.github.com/orgs/celestialbeings")
        assert(org.repos_url == "https://api.github.com/orgs/celestialbeings/repos")
        assert(org.events_url == "https://api.github.com/orgs/celestialbeings/events")
        assert(org.members_url == "https://api.github.com/orgs/celestialbeings/members{/member}")
        assert(org.public_members_url == "https://api.github.com/orgs/celestialbeings/public_members{/member}")
        assert(org.avatar_url.length > 0)
        assert(org.description == "No description")
        assert(org.name == "celestialbeings")
        assert(org.company.get == "givery")
        assert(org.blog == "")
        assert(org.location == "Tokyo")
        assert(org.email == "")
        assert(org.public_repos == 2)
        assert(org.public_gists == 0)
        assert(org.followers == 0)
        assert(org.following == 0)
        assert(org.html_url == "https://github.com/celestialbeings")
        assert(org.created_at.toDateTime(DateTimeZone.UTC) == DateTime.parse("2015-05-13T06:28:23Z").toDateTime(DateTimeZone.UTC))
        assert(org.updated_at.toDateTime(DateTimeZone.UTC).getMillis() - DateTime.now(DateTimeZone.UTC).getMillis() <= 5000)
        assert(org.`type` == "Organization")
      }
    }

    it("should return None if invalid organization.") {
      assert(Await.result(api.getOrganization(organizationInvalid), TIMEOUT).isEmpty)
    }
  }
}
