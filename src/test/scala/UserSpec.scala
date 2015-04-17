import org.scalatest.FunSpec

import org.json4s.jackson.JsonMethods
import codecheck.github.models.User

class UserSpec extends FunSpec with Constants {

  describe("user") {
    val user1 = """
      { "name": "user1"}
    """
    val user2 = """
      { "login": "user2"}
    """

    it("should get name from name field.") {
      val u = new User(JsonMethods.parse(user1))
      assert(u.name == "user1")
    }
    it("should get name from login field.") {
      val u = new User(JsonMethods.parse(user2))
      assert(u.name == "user2")
    }
  }

  
}
