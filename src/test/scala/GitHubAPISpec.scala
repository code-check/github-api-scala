import org.scalatest.FunSpec
import scala.concurrent.Await

class GitHubAPISpec extends FunSpec with Constants {

  describe("org method") {
    it("should be return multiple organizations.") {
      val result = Await.result(API.org("shunjikonishi"), TIMEOUT)
      println(result)
    }
  }


}
