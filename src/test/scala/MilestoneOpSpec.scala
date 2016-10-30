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
    val list = Await.result(api.listMilestones(user, userRepo, MilestoneListOption(state=MilestoneState.all)), TIMEOUT)
    list.foreach { m =>
      Await.result(api.removeMilestone(user, userRepo, m.number), TIMEOUT)
    }
  }
  private def create(input: MilestoneInput): Milestone = {
    Await.result(api.createMilestone(user, userRepo, input), TIMEOUT)
  }

  describe("createMilestone") {
    removeAll
    val gName = generateRandomString
    val gDescription = generateRandomString
    val d1 = DateTime.now().plusMonths(1).withMillisOfSecond(0)

    it("without description and due_on should succeed") {
      val input = MilestoneInput(gName)
      val m = Await.result(api.createMilestone(user, userRepo, input), TIMEOUT)
      assert(m.title == gName)
      assert(m.state == MilestoneState.open)
      assert(m.description.isEmpty)
      assert(m.due_on.isEmpty)
    }
    it("without due_on should succeed") {
      val input = MilestoneInput(gName, gDescription)
      val m = Await.result(api.createMilestone(user, userRepo, input), TIMEOUT)
      assert(m.title == gName)
      assert(m.state == MilestoneState.open)
      assert(m.description.get == gDescription)
      assert(m.due_on.isEmpty)
    }
    it("without description should succeed") {
      val input = MilestoneInput(gName, d1)
      val m = Await.result(api.createMilestone(user, userRepo, input), TIMEOUT)
      assert(m.title == gName)
      assert(m.state == MilestoneState.open)
      assert(m.description.isEmpty)
//      assert(m.due_on.get == d1)
    }
    it("with description and due_on should succeed") {
      val input = MilestoneInput(gName, gDescription, d1)
      val m = Await.result(api.createMilestone(user, userRepo, input), TIMEOUT)
      assert(m.title == gName)
      assert(m.state == MilestoneState.open)
      assert(m.description.get == gDescription)
//      assert(m.due_on.get == d1)
    }
    it("with wrong reponame should fail") {
      val input = MilestoneInput(gName, gDescription, d1)
      val ex = Await.result(api.createMilestone(user, repoInvalid, input).failed, TIMEOUT)
      ex match {
        case e: NotFoundException =>
        case _ => fail
      }
    }
  }
  describe("getMilestone") {
    removeAll
    val gName = generateRandomString
    val gDescription = generateRandomString
    val d1 = DateTime.now().plusMonths(1).withMillisOfSecond(0)
    val m1 = create(MilestoneInput(gName, gDescription, d1))

    it("should succeed") {
      Await.result(api.getMilestone(user, userRepo, m1.number), TIMEOUT).map { m =>
        assert(m.url == m1.url)
        assert(m.id == m1.id)
        assert(m.number == m1.number)
        assert(m.state == MilestoneState.open)
        assert(m.title == gName)
        assert(m.description.get == gDescription)
        assert(m.creator.login == m1.creator.login)
        assert(m.open_issues == 0)
        assert(m.closed_issues == 0)
        assert(m.created_at != null)
        assert(m.updated_at != null)
        assert(m.closed_at.isEmpty)
//        assert(m.due_on.get == d1)
      }
    }
    it("should be None") {
      assert(Await.result(api.getMilestone(user, userRepo, 999), TIMEOUT).isEmpty)
    }
  }
  describe("updateMilestone") {
    val gName1 = generateRandomString
    val gDescription1 = generateRandomString
    val d1 = DateTime.now().plusMonths(1).withMillisOfSecond(0)
    val gName2 = generateRandomString
    val gDescription2 = generateRandomString
    val d2 = d1.plusMonths(1)

    removeAll
    val m1 = create(MilestoneInput(gName1, gDescription1, d1))

    it("should succeed") {
      val input = MilestoneInput(
        title=Some(gName2),
        state=Some(MilestoneState.closed),
        description=Some(gDescription2),
        due_on=Some(d2)
      )
      val m = Await.result(api.updateMilestone(user, userRepo, m1.number, input), TIMEOUT)
      assert(m.id == m1.id)
      assert(m.number == m1.number)
      assert(m.state == MilestoneState.closed)
      assert(m.title == gName2)
      assert(m.description.get == gDescription2)
      assert(m.closed_at.isDefined)
//      assert(m.due_on.get == d2)

      Await.result(api.getMilestone(user, userRepo, m.number), TIMEOUT).map { m2=>
        assert(m2.id == m1.id)
        assert(m2.number == m1.number)
        assert(m2.state == MilestoneState.closed)
        assert(m2.title == gName2)
        assert(m2.description.get == gDescription2)
        assert(m2.closed_at.isDefined)
//        assert(m2.due_on.get == d2)
      }
    }
  }
  describe("listMilestones") {
    val gName1 = generateRandomString
    val gDescription1 = generateRandomString
    val d1 = DateTime.now().plusMonths(1).withMillisOfSecond(0)
    val gName2 = generateRandomString
    val gDescription2 = generateRandomString
    val d2 = d1.plusMonths(1)

    removeAll
    val m1 = create(MilestoneInput(gName1, gDescription1, d1))
    val m2 = create(MilestoneInput(gName2, gDescription2, d2))

    it("should succeed") {
      val list = Await.result(api.listMilestones(user, userRepo), TIMEOUT)
      assert(list.size == 2)
      val m = list.head
      assert(m.title == gName1)
//      assert(m.due_on.get == d1)
    }
    it("with sort desc should succeed") {
      val option = MilestoneListOption(direction=SortDirection.desc)
      val list = Await.result(api.listMilestones(user, userRepo, option), TIMEOUT)
      assert(list.size == 2)
      val m = list.head
      assert(m.title == gName2)
//      assert(m.due_on.get == d2)
    }
    it("with wrong reponame should fail") {
      val ex = Await.result(api.listMilestones(user, repoInvalid).failed, TIMEOUT)
      ex match {
        case e: NotFoundException =>
        case _ => fail
      }
    }
  }
  describe("removeMilestone") {
    val gName = generateRandomString
    val gDescription = generateRandomString
    val d1 = DateTime.now().plusMonths(1).withMillisOfSecond(0)

    removeAll
    val m1 = create(MilestoneInput(gName, gDescription, d1))

    it("should succeed") {
      val b = Await.result(api.removeMilestone(user, userRepo, m1.number), TIMEOUT)
      assert(b)

      assert(Await.result(api.getMilestone(user, userRepo, m1.number), TIMEOUT).isEmpty)

      val ex = Await.result(api.removeMilestone(user, userRepo, m1.number).failed, TIMEOUT)
      ex match {
        case e: NotFoundException =>
        case _ => fail
      }
    }
  }
}
