import org.scalatest.FunSpec
import org.scalatest.BeforeAndAfterAll
import scala.concurrent.Await
import org.joda.time.DateTime
import org.joda.time.DateTimeZone

import codecheck.github.models.IssueListOption
import codecheck.github.models.IssueFilter
import codecheck.github.models.IssueListOption4Repository
import codecheck.github.models.IssueState
import codecheck.github.models.Issue
import codecheck.github.models.IssueInput
import codecheck.github.models.MilestoneSearchOption

import codecheck.github.models.MilestoneInput
import codecheck.github.models.MilestoneListOption
import codecheck.github.models.MilestoneState
import codecheck.github.models.Milestone

class IssueOpSpec extends FunSpec with Constants with BeforeAndAfterAll {

  val number = 1
  var nUser: Long = 0
  var nOrg: Long = 0
  var nTime: DateTime = DateTime.now()
  val tRepo = repo + "2"

  override def beforeAll() {
    val userMilestones = Await.result(api.listMilestones(user, userRepo, MilestoneListOption(state=MilestoneState.all)), TIMEOUT)
    userMilestones.foreach { m =>
      Await.result(api.removeMilestone(user, userRepo, m.number), TIMEOUT)
    }

    val orgMilestones = Await.result(api.listMilestones(organization, tRepo, MilestoneListOption(state=MilestoneState.all)), TIMEOUT)
    orgMilestones.foreach { m =>
      Await.result(api.removeMilestone(organization, tRepo, m.number), TIMEOUT)
    }

    val nInput = new MilestoneInput(Some("test milestone"))
    val nInput2 = new MilestoneInput(Some("test milestone 2"))

    Await.result(api.createMilestone(user, userRepo, nInput), TIMEOUT)
    Await.result(api.createMilestone(user, userRepo, nInput2), TIMEOUT)

    Await.result(api.createMilestone(organization, tRepo, nInput), TIMEOUT)
    Await.result(api.createMilestone(organization, tRepo, nInput2), TIMEOUT)
  }

  describe("createIssue(owner, repo, input)") {
    val input = IssueInput(Some("test issue"), Some("testing"), Some(user), Some(1), Seq("question"))

    it("should create issue for user's own repo.") {
      val result = Await.result(api.createIssue(user, userRepo, input), TIMEOUT)
      nUser = result.number
      assert(result.url == "https://api.github.com/repos/" + user + "/" + userRepo + "/issues/" + nUser)
      assert(result.labels_url == "https://api.github.com/repos/" + user + "/" + userRepo + "/issues/" + nUser + "/labels{/name}")
      assert(result.comments_url == "https://api.github.com/repos/" + user + "/" + userRepo + "/issues/" + nUser + "/comments")
      assert(result.events_url == "https://api.github.com/repos/" + user + "/" + userRepo + "/issues/" + nUser + "/events")
      assert(result.html_url == "https://github.com/" + user + "/" + userRepo + "/issues/" + nUser)
      assert(result.title == "test issue")
      assert(result.user.login == user)
      assert(result.labels.head.name == "question")
      assert(result.state == "open")
      assert(result.locked == false)
      assert(result.assignee.get.login == user)
      assert(result.milestone.get.number == 1)
      assert(result.comments == 0)
      assert(result.created_at.toDateTime(DateTimeZone.UTC).getMillis() - DateTime.now(DateTimeZone.UTC).getMillis() <= 5000)
      assert(result.updated_at.toDateTime(DateTimeZone.UTC).getMillis() - DateTime.now(DateTimeZone.UTC).getMillis() <= 5000)
      assert(result.closed_at.isEmpty)
      assert(result.body.get == "testing")
      assert(result.closed_by.isEmpty)
    }

    it("should create issue for organization's repo.") {
      val result = Await.result(api.createIssue(organization, tRepo, input), TIMEOUT)
      nOrg = result.number
      assert(result.url == "https://api.github.com/repos/" + organization + "/" + tRepo + "/issues/" + nOrg)
      assert(result.labels_url == "https://api.github.com/repos/" + organization + "/" + tRepo + "/issues/" + nOrg + "/labels{/name}")
      assert(result.comments_url == "https://api.github.com/repos/" + organization + "/" + tRepo+ "/issues/" + nOrg + "/comments")
      assert(result.events_url == "https://api.github.com/repos/" + organization + "/" + tRepo + "/issues/" + nOrg + "/events")
      assert(result.html_url == "https://github.com/" + organization + "/" + tRepo + "/issues/" + nOrg)
      assert(result.title == "test issue")
      assert(result.user.login == user)
      assert(result.labels.head.name == "question")
      assert(result.state == "open")
      assert(result.locked == false)
      assert(result.assignee.get.login == user)
      assert(result.milestone.get.number == 1)
      assert(result.comments == 0)
      assert(result.created_at.toDateTime(DateTimeZone.UTC).getMillis() - DateTime.now(DateTimeZone.UTC).getMillis() <= 5000)
      assert(result.updated_at.toDateTime(DateTimeZone.UTC).getMillis() - DateTime.now(DateTimeZone.UTC).getMillis() <= 5000)
      assert(result.body.get == "testing")
      assert(result.closed_by.isEmpty)
    }
  }

  describe("getIssue(owner, repo, number)") {
    it("should return issue from user's own repo.") {
      val result = Await.result(api.getIssue(user, userRepo, nUser), TIMEOUT)
      assert(result.get.title == "test issue")
    }

    it("should return issue from organization's repo.") {
      val result = Await.result(api.getIssue(organization, tRepo, nOrg), TIMEOUT)
      assert(result.get.title == "test issue")
    }
  }

  describe("unassign(owner, repo, number)") {
    it("should succeed with valid inputs on issues in user's own repo.") {
      val result = Await.result(api.unassign(user, userRepo, nUser), TIMEOUT)
      assert(result.opt("assignee").isEmpty)
    }

    it("should succeed with valid inputs on issues in organization's repo.") {
      val result = Await.result(api.unassign(organization, tRepo, nOrg), TIMEOUT)
      assert(result.opt("assignee").isEmpty)
    }
  }

  describe("assign(owner, repo, number, assignee)") {
    it("should succeed with valid inputs on issues in user's own repo.") {
      val result = Await.result(api.assign(user, userRepo, nUser, user), TIMEOUT)
      assert(result.get("assignee.login") == user)
    }

    it("should succeed with valid inputs on issues in organization's repo.") {
      val result = Await.result(api.assign(organization, tRepo, nOrg, user), TIMEOUT)
      assert(result.get("assignee.login") == user)
    }
  }

  describe("listAllIssues(option)") {
    it("shold return at least one issue.") {
      val result = Await.result(api.listAllIssues(), TIMEOUT)
      assert(result.length > 0)
    }

    it("shold return only two issues when using options.") {
      val option = IssueListOption(IssueFilter.created, IssueState.open, Seq("question"), since=Some(nTime))
      val result = Await.result(api.listAllIssues(option), TIMEOUT)
      assert(result.length == 2)
      assert(result.head.title == "test issue")
    }
  }

  describe("listUserIssues(option)") {
    it("shold return at least one issue.") {
      val result = Await.result(api.listUserIssues(), TIMEOUT)
      assert(result.length > 0)
    }

    it("shold return only one issues when using options.") {
      val option = IssueListOption(IssueFilter.created, IssueState.open, Seq("question"), since=Some(nTime))
      val result = Await.result(api.listUserIssues(option), TIMEOUT)
      assert(result.length == 1)
      assert(result.head.title == "test issue")
    }
  }

  describe("listOrgIssues(org, option)") {
    it("should return at least one issue.") {
      val result = Await.result(api.listOrgIssues(organization), TIMEOUT)
      assert(result.length > 0)
    }

    it("shold return only one issues when using options.") {
      val option = IssueListOption(IssueFilter.created, IssueState.open, Seq("question"), since=Some(nTime))
      val result = Await.result(api.listOrgIssues(organization, option), TIMEOUT)
      assert(result.length == 1)
      assert(result.head.title == "test issue")
    }
  }

  describe("listRepositoryIssues(owner, repo, option)") {
    it("should return at least one issue from user's own repo.") {
      val result = Await.result(api.listRepositoryIssues(user, userRepo), TIMEOUT)
      assert(result.length > 0)
    }

    it("should return at least one issue from organization's repo.") {
      val result = Await.result(api.listRepositoryIssues(organization, tRepo), TIMEOUT)
      assert(result.length > 0)
    }

    it("should return only one issue from user's own repo when using options.") {
      val option = new IssueListOption4Repository(Some(MilestoneSearchOption(1)), IssueState.open, Some(user), Some(user), labels=Seq("question"), since=Some(nTime))
      val result = Await.result(api.listRepositoryIssues(user, userRepo, option), TIMEOUT)
      //showResponse(option.q)
      assert(result.length == 1)
      assert(result.head.title == "test issue")
    }

    it("should return only one issue from organization's repo when using options.") {
      val option = new IssueListOption4Repository(Some(MilestoneSearchOption(1)), IssueState.open, Some(user), Some(user), labels=Seq("question"), since=Some(nTime))
      val result = Await.result(api.listRepositoryIssues(organization, tRepo, option), TIMEOUT)
      assert(result.length == 1)
      assert(result.head.title == "test issue")
    }
  }

  describe("editIssue(owner, repo, number, input)") {
    val input = IssueInput(Some("test issue edited"), Some("testing again"), Some(user), Some(2), Seq("question", "bug"), Some(IssueState.closed))

    it("should edit the issue in user's own repo.") {
      val result = Await.result(api.editIssue(user, userRepo, nUser, input), TIMEOUT)
      assert(result.title == "test issue edited")
      assert(result.body.get == "testing again")
      assert(result.milestone.get.number == 2)
      assert(result.labels.head.name == "bug")
      assert(result.state == "closed")
      assert(result.updated_at.toDateTime(DateTimeZone.UTC).getMillis() - DateTime.now(DateTimeZone.UTC).getMillis() <= 5000)
    }

    it("should edit the issue in organization's repo.") {
      val result = Await.result(api.editIssue(organization, tRepo, nOrg, input), TIMEOUT)
      assert(result.title == "test issue edited")
      assert(result.body.get == "testing again")
      assert(result.milestone.get.number == 2)
      assert(result.labels.head.name == "bug")
      assert(result.state == "closed")
      assert(result.updated_at.toDateTime(DateTimeZone.UTC).getMillis() - DateTime.now(DateTimeZone.UTC).getMillis() <= 5000)
    }
  }
}
