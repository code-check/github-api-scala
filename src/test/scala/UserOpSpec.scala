package codecheck.github
package operations

import exceptions._
import models._

import org.scalatest.FunSpec
import org.scalatest.BeforeAndAfterAll
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global

class UserOpSpec extends FunSpec
  with api.Constants
  with BeforeAndAfterAll
{
  val origin = Await.result(api.getAuthenticatedUser, TIMEOUT)

  override def afterAll(): Unit = {
    val input = UserInput(
      origin.name.orElse(Some("")),
      origin.email.orElse(Some("")),
      origin.blog.orElse(Some("")),
      origin.company.orElse(Some("")),
      origin.location.orElse(Some("")),
      Some(origin.hireable),
      origin.bio.orElse(Some(""))
    )
    val user = Await.result(api.updateAuthenticatedUser(input), TIMEOUT)
  }
  describe("getUser") {
    it("with valid username should succeed") {
      val userOp = Await.result(api.getUser("sukeshni"), TIMEOUT)
      assert(userOp.isDefined)
      val user = userOp.get
      assert(user.login == "sukeshni")
    }
    it("with invalid username should be None") {
      val userOp = Await.result(api.getUser("sukeshni-wrong"), TIMEOUT)
      assert(userOp.isEmpty)
    }
  }

  describe("updateAuthenticatedUser") {
    it("if values updated correctly should succeed") {
      val input = new UserInput(
        Some("firstname lastname"),
        Some("test@givery.co.jp"),
        Some("Blog"),
        Some("Anywhere"),
        Some("Somewhere"),
        Some(!origin.hireable),
        Some("bio")
      )
      val res = Await.result(api.updateAuthenticatedUser(input), TIMEOUT)
      assert(res.name.get == input.name.get)
      assert(res.email.getOrElse("") == input.email.get)
      assert(res.blog.get == input.blog.get)
      assert(res.company.get == input.company.get)
      assert(res.location.get == input.location.get)
      assert(res.bio.get == input.bio.get)
    }
  }

  describe("getAllUsers") {
    it("with no since parameter it should succeed") {
      val res = Await.result(api.getAllUsers(), TIMEOUT)
      showResponse(res)
      assert(res{0}.id == 1)
    }
    it("with valid since parameter it should succeed") {
      val userOp = Await.result(api.getUser("sukeshni"), TIMEOUT)//give a valid username
      assert(userOp.isDefined)
      val userOpGet = userOp.get
      val res = Await.result(api.getAllUsers(userOpGet.id - 1), TIMEOUT)
      showResponse(res)
      assert((res{0}.id).toLong == userOpGet.id)
    }
  }
}
