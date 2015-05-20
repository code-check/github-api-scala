
import org.scalatest.FunSpec
import org.scalatest.BeforeAndAfter
import scala.concurrent.Await

import codecheck.github.models.Webhook
import codecheck.github.models.WebhookConfig
import codecheck.github.models.WebhookInput
import codecheck.github.models.WebhookUpdateInput


class WebhookOpSpec extends FunSpec with Constants with BeforeAndAfter {

	val gURL = generateRandomWord;
  val gURL2 = generateRandomWord;
	var nID: Long = 0;

	describe("listWebhooks(owner, repo)") {
	    it("should succeed with valid owner, repo.") {
	    	val result = Await.result(api.listWebhooks(organization, repo), TIMEOUT)
	    	assert(result.length > 0)
	    }

      it("should fail if there are no webhookswith valid owner, repo.") {
        val result = Await.result(api.listWebhooks(organization, repo), TIMEOUT)
        assert(result.length > 0)
      }
  	}

	describe("createWebhook(owner, repo, input)") {
		it("should succeed with valid organization, repo, and inputs.") {
			val config = new WebhookConfig("testsite.com/" + gURL)
			val input = new WebhookInput("web", config, events=Seq("*"))
			Await.result(api.createWebhook(organization, repo, input), TIMEOUT).map { res =>
				nID = res.id
        assert(res.url == "https://api.github.com/repos/" + organization + "/" + repo + "/hooks/" + nID)
        assert(res.test_url == "https://api.github.com/repos/" + organization + "/" + repo + "/hooks/" + nID + "/test")
        assert(res.ping_url == "https://api.github.com/repos/" + organization + "/" + repo + "/hooks/" + nID + "/pings")
        assert(res.name == "web")
        assert(res.events == Seq("*"))
				assert(res.active == true)
        assert(res.config.url == "testsite.com/" + gURL)
        assert(res.config.content_type == "json")
        assert(res.config.secret == "")
        assert(res.config.insecure_ssl == "0")
			}
		}
	} 

	describe("getWebhook(owner, repo, id)") {
		it("should succeed with valid organization, repo and id.") {
			Await.result(api.getWebhook(organization, repo, nID), TIMEOUT).map { res =>
    		assert(res.id == nID)
    	}
		}
	}
	
	describe("updateWebhook(owner, repo, id, input)") {
		it("should succeed updating by rewriting events.") {
			val input = new WebhookUpdateInput(events=Some(Seq("create", "pull_request")))
			Await.result(api.updateWebhook(organization, repo, nID, input), TIMEOUT).map { res =>
        showResponse(res)
        assert(res.events == Seq("create", "pull_request"))
			}
		}

    it("should succeed updating by rewriting config.") {
      val config = new WebhookConfig("testsite.com/" + gURL2)
      val input = new WebhookUpdateInput(Some(config))
      Await.result(api.updateWebhook(organization, repo, nID, input), TIMEOUT).map { res =>
        assert(res.config.url == "testsite.com/" + gURL2)
      }
    }

    it("should succeed updating by using add_events.") {
      val input = new WebhookUpdateInput(add_events=Some(Seq("push")))
      Await.result(api.updateWebhook(organization, repo, nID, input), TIMEOUT).map { res =>
        assert(res.config.url == "testsite.com/" + gURL2)
        assert(res.events == Seq("create", "pull_request", "push"))
      }
    }

    it("should succeed updating by using remove_events.") {
      val input = new WebhookUpdateInput(remove_events=Some(Seq("pull_request")))
      Await.result(api.updateWebhook(organization, repo, nID, input), TIMEOUT).map { res =>
        assert(res.config.url == "testsite.com/" + gURL2)
        assert(res.events == Seq("create", "push"))
      }
    }
	} 

	describe("testWebhook(owner, repo, id)") {
		it("should succeed with valid inputs.") {
			val result = Await.result(api.testWebhook(organization, repo, nID), TIMEOUT)
			assert(result == true)
		}
	} 

	describe("pingWebhook(owner, repo, id)") {
		it("should succeed with valid inputs.") {
			val result = Await.result(api.pingWebhook(organization, repo, nID), TIMEOUT)
			assert(result == true)
		}
	} 

	describe("removeWebhook(owner, repo, id)") {
		it("should succeed with valid inputs.") {
			val result = Await.result(api.removeWebhook(organization, repo, nID), TIMEOUT)
			assert(result == true)
		}
	} 
}