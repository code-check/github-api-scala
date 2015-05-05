import org.scalatest.path.FunSpec
import codecheck.github.exceptions.NotFoundException
import codecheck.github.models.Milestone
import codecheck.github.models.MilestoneInput
import codecheck.github.models.MilestoneListOption
import codecheck.github.models.MilestoneState
import codecheck.github.models.SortDirection
import codecheck.github.exceptions.GitHubAPIException
import codecheck.github.exceptions.NotFoundException
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import org.joda.time.DateTime

class MilestoneOpSpec extends FunSpec 
  with Constants 
{

  private def removeAll = {
    val list = Await.result(api.listMilestones(owner, repo, MilestoneListOption(state=MilestoneState.all)), TIMEOUT)
    list.foreach { m =>
      Await.result(api.removeMilestone(owner, repo, m.number), TIMEOUT)
    }
  }
  private def create(input: MilestoneInput): Milestone = {
    Await.result(api.createMilestone(owner, repo, input), TIMEOUT)
  }

  describe("createMilestone") {
    removeAll
    val d1 = DateTime.now().plusMonths(1).withMillisOfSecond(0)
    it("without description and due_on should succeed") {
      val input = MilestoneInput("test1")
      val m = Await.result(api.createMilestone(owner, repo, input), TIMEOUT)
      assert(m.title == "test1")
      assert(m.state == MilestoneState.open)
      assert(m.description.isEmpty)
      assert(m.due_on.isEmpty)
    }
    it("without due_on should succeed") {
      val input = MilestoneInput("test2", "description")
      val m = Await.result(api.createMilestone(owner, repo, input), TIMEOUT)
      assert(m.title == "test2")
      assert(m.state == MilestoneState.open)
      assert(m.description.get == "description")
      assert(m.due_on.isEmpty)
    }
    it("without description should succeed") {
      val input = MilestoneInput("test3", d1)
      val m = Await.result(api.createMilestone(owner, repo, input), TIMEOUT)
      assert(m.title == "test3")
      assert(m.state == MilestoneState.open)
      assert(m.description.isEmpty)
      assert(m.due_on.get == d1)
    }
    it("with description and due_on should succeed") {
      val input = MilestoneInput("test4", "description", d1)
      val m = Await.result(api.createMilestone(owner, repo, input), TIMEOUT)
      assert(m.title == "test4")
      assert(m.state == MilestoneState.open)
      assert(m.description.get == "description")
      assert(m.due_on.get == d1)
    }
    it("with wrong reponame should fail") {
      val input = MilestoneInput("test5", "description", d1)
      val ex = Await.result(api.createMilestone(owner, repo + "-unknown", input).failed, TIMEOUT)
      ex match {
        case e: NotFoundException => 
        case _ => fail
      }
    }
  }
  describe("getMilestone") {
    val d1 = DateTime.now().plusMonths(1).withMillisOfSecond(0)

    removeAll
    val m1 = create(MilestoneInput("test", "test", d1))

    it("should succeed") {
      Await.result(api.getMilestone(owner, repo, m1.number), TIMEOUT).map { m =>
        assert(m.url == m1.url)
        assert(m.id == m1.id)
        assert(m.number == m1.number)
        assert(m.state == MilestoneState.open)
        assert(m.title == "test")
        assert(m.description.get == "test")
        assert(m.creator.login == m1.creator.login)
        assert(m.open_issues == 0)
        assert(m.closed_issues == 0)
        assert(m.created_at != null)
        assert(m.updated_at != null)
        assert(m.closed_at.isEmpty)
        assert(m.due_on.get == d1)
      }
    }
    it("should be None") {
      assert(Await.result(api.getMilestone(owner, repo, 999), TIMEOUT).isEmpty)
    }
  }
  describe("updateMilestone") {
    val d1 = DateTime.now().plusMonths(1).withMillisOfSecond(0)
    val d2 = d1.plusMonths(1)

    removeAll
    val m1 = create(MilestoneInput("test", "test", d1))

    it("should succeed") {
      val input = MilestoneInput(
        title=Some("test2"),
        state=Some(MilestoneState.closed),
        description=Some("description"),
        due_on=Some(d2)
      )
      val m = Await.result(api.updateMilestone(owner, repo, m1.number, input), TIMEOUT)
      assert(m.id == m1.id)
      assert(m.number == m1.number)
      assert(m.state == MilestoneState.closed)
      assert(m.title == "test2")
      assert(m.description.get == "description")
      assert(m.closed_at.isDefined)
      assert(m.due_on.get == d2)

      Await.result(api.getMilestone(owner, repo, m.number), TIMEOUT).map { m2=>
        assert(m2.id == m1.id)
        assert(m2.number == m1.number)
        assert(m2.state == MilestoneState.closed)
        assert(m2.title == "test2")
        assert(m2.description.get == "description")
        assert(m2.closed_at.isDefined)
        assert(m2.due_on.get == d2)
      }
    }
  }
  describe("listMilestones") {
    val d1 = DateTime.now().plusMonths(1).withMillisOfSecond(0)
    val d2 = d1.plusMonths(1)

    removeAll
    val m1 = create(MilestoneInput("test", "test", d1))
    val m2 = create(MilestoneInput("test2", "test2", d2))

    it("should succeed") {
      val list = Await.result(api.listMilestones(owner, repo), TIMEOUT)
      assert(list.size == 2)
      val m = list.head
      assert(m.title == "test")
      assert(m.due_on.get == d1)
    }
    it("with sort desc should succeed") {
      val option = MilestoneListOption(direction=SortDirection.desc)
      val list = Await.result(api.listMilestones(owner, repo, option), TIMEOUT)
      assert(list.size == 2)
      val m = list.head
      assert(m.title == "test2")
      assert(m.due_on.get == d2)
    }
    it("with wrong reponame should fail") {
      val ex = Await.result(api.listMilestones(owner, repo + "-unknown").failed, TIMEOUT)
      ex match {
        case e: NotFoundException => 
        case _ => fail
      }
    }
  }
  describe("removeMilestone") {
    val d1 = DateTime.now().plusMonths(1).withMillisOfSecond(0)

    removeAll
    val m1 = create(MilestoneInput("test", "test", d1))

    it("should succeed") {
      val b = Await.result(api.removeMilestone(owner, repo, m1.number), TIMEOUT)
      assert(b)

      assert(Await.result(api.getMilestone(owner, repo, m1.number), TIMEOUT).isEmpty)

      val ex = Await.result(api.removeMilestone(owner, repo, m1.number).failed, TIMEOUT)
      ex match {
        case e: NotFoundException => 
        case _ => fail
      }
    }
  }
}
