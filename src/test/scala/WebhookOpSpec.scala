
import org.scalatest.FunSpec
import org.scalatest.BeforeAndAfter
import scala.concurrent.Await

import codecheck.github.models.Webhook
import codecheck.github.models.WebhookConfig
import codecheck.github.models.WebhookCreateInput
import codecheck.github.models.WebhookInput


class WebhookOpSpec extends FunSpec with Constants with BeforeAndAfter {

  val targetURL = "http://github-hook.herokuapp.com/hook"
	var nID: Long = 0;

	describe("listWebhooks(owner, repo)") {
	    it("should succeed with valid owner, repo.") {
	    	val result = Await.result(api.listWebhooks(organization, repo), TIMEOUT)
	    	assert(result.length > 0)
	    }
  	}

	describe("createWebhook(owner, repo, input)") {
		it("should succeed with valid organization, repo, and inputs.") {
			val config = new WebhookConfig(targetURL)
			val input = new WebhookCreateInput("web", config, events=Seq("*"))
			val res = Await.result(api.createWebhook(organization, repo, input), TIMEOUT)
      showResponse(res)
			nID = res.id
      assert(res.url == "https://api.github.com/repos/" + organization + "/" + repo + "/hooks/" + nID)
      assert(res.test_url == "https://api.github.com/repos/" + organization + "/" + repo + "/hooks/" + nID + "/test")
      assert(res.ping_url == "https://api.github.com/repos/" + organization + "/" + repo + "/hooks/" + nID + "/pings")
      assert(res.name == "web")
      assert(res.events == Seq("*"))
			assert(res.active == true)
      assert(res.config.url == targetURL)
      assert(res.config.content_type == "json")
      assert(res.config.secret == "")
      assert(res.config.insecure_ssl == "0")
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
			val input = new WebhookInput(events=Some(Seq("create", "pull_request")))
			val res = Await.result(api.updateWebhook(organization, repo, nID, input), TIMEOUT)
      assert(res.events == Seq("create", "pull_request"))
		}

    it("should succeed updating by using add_events.") {
      val input = new WebhookInput(add_events=Some(Seq("push")))
      val res = Await.result(api.updateWebhook(organization, repo, nID, input), TIMEOUT)
      assert(res.config.url == targetURL)
      assert(res.events == Seq("create", "pull_request", "push"))
    }

    it("should succeed updating by using remove_events.") {
      val input = new WebhookInput(remove_events=Some(Seq("pull_request")))
      val res = Await.result(api.updateWebhook(organization, repo, nID, input), TIMEOUT)
      assert(res.config.url == targetURL)
      assert(res.events == Seq("create", "push"))
    }

    it("should succeed updating by rewriting config.") {
      val config = new WebhookConfig(targetURL)
      val input = new WebhookInput(Some(config))
      val res = Await.result(api.updateWebhook(organization, repo, nID, input), TIMEOUT)
      assert(res.config.url == targetURL)
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