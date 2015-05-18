
import org.scalatest.FunSpec
import org.scalatest.BeforeAndAfter
import scala.concurrent.Await

import codecheck.github.models.Webhook

class WebhookOpSpec extends FunSpec with Constants with BeforeAndAfter {

	describe("listWebhooks(owner, repo)") {
	    it("should succeed with valid owner, repo.") {
	      val result = Await.result(api.listWebhooks("code-check", "github-api-scala"), TIMEOUT)
	      assert(result.length > 0)
	    }
  	}
}