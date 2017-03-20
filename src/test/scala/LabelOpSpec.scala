package codecheck.github
package operations

import exceptions._
import models._

import org.scalatest.FunSpec
import scala.concurrent.Await

class LabelOpSpec extends FunSpec with api.Constants {

  val number = 1
  val gName = generateRandomWord

  describe("removeAllLabels") {
    it("should succeed") {
      val result = Await.result(api.removeAllLabels(user, userRepo, number), TIMEOUT)
      assert(result.length == 0)
    }
  }

  describe("addLabel") {
    it("should succeed") {
      val result = Await.result(api.addLabels(user, userRepo, number, "bug"), TIMEOUT)
      assert(result.length == 1)
      val label = result.head
      assert(label.name == "bug")
      assert(label.url.length > 0)
      assert(label.color.length == 6)
    }
  }

  describe("replaceLabels") {
    it("should succeed") {
      val result = Await.result(api.replaceLabels(user, userRepo, number, "duplicate", "invalid"), TIMEOUT)
      assert(result.length == 2)
      assert(result.filter(_.name == "duplicate").length == 1)
      assert(result.filter(_.name == "invalid").length == 1)
    }
  }

  describe("removeLabel") {
    it("should succeed") {
      val result = Await.result(api.removeLabel(user, userRepo, number, "duplicate"), TIMEOUT)
      assert(result.length == 1)

      val result2 = Await.result(api.removeLabel(user, userRepo, number, "invalid"), TIMEOUT)
      assert(result2.length == 0)
    }
  }

  describe("listLabels") {
    it("listLabels should succeed") {
      Await.result(api.addLabels(user, userRepo, number, "invalid"), TIMEOUT)
      val result = Await.result(api.listLabels(user, userRepo, number), TIMEOUT)
      assert(result.length == 1)
      val label = result.head
      assert(label.name == "invalid")
      assert(label.url.length > 0)
      assert(label.color.length == 6)
    }
  }

  describe("listLabelDefs") {
    it("should succeed") {
      val result = Await.result(api.listLabelDefs(user, userRepo), TIMEOUT)
      assert(result.length > 0)
      val label = result.head
      assert(label.name.length > 0)
      assert(label.url.length > 0)
      assert(label.color.length == 6)
    }
  }

  describe("getLabelDef") {
    it("should succeed") {
      Await.result(api.getLabelDef(user, userRepo, "question"), TIMEOUT).map { label =>
        assert(label.name == "question")
        assert(label.url.length > 0)
        assert(label.color == "cc317c")
      }
    }
    it("should be None") {
      assert(Await.result(api.getLabelDef(user, userRepo, "hoge"), TIMEOUT).isEmpty)
    }
  }

  describe("createLabelDef") {
    it("should succeed") {
      val input = LabelInput(gName, "cc317c")
      val label = Await.result(api.createLabelDef(user, userRepo, input), TIMEOUT)
      assert(label.name == gName)
      assert(label.url.length > 0)
      assert(label.color == "cc317c")
    }
    it("again should fail") {
      val input = LabelInput(gName, "cc317c")
      try {
        val label = Await.result(api.createLabelDef(user, userRepo, input), TIMEOUT)
        fail
      } catch {
        case e: GitHubAPIException =>
          assert(e.error.errors.length == 1)
          assert(e.error.errors.head.code == "already_exists")
      }
    }
  }

  describe("updateLabelDef") {
    it("should succeed") {
      val input = LabelInput(gName, "84b6eb")
      val label = Await.result(api.updateLabelDef(user, userRepo, gName, input), TIMEOUT)
      assert(label.name == gName)
      assert(label.url.length > 0)
      assert(label.color == "84b6eb")
    }
  }

  describe("removeLabelDef") {
    it("should succeed") {
      val result = Await.result(api.removeLabelDef(user, userRepo, gName), TIMEOUT)
      assert(result)
    }
    it("removeLabelDef again should fail") {
      try {
        val result = Await.result(api.removeLabelDef(user, userRepo, gName), TIMEOUT)
        fail
      } catch {
        case e: NotFoundException =>
          assert(e.error.errors.length == 0)
      }
    }
  }
}
