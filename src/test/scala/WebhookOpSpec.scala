
import org.scalatest.FunSpec
import org.scalatest.BeforeAndAfter
import scala.concurrent.Await

import codecheck.github.models.Webhook
import codecheck.github.models.WebhookConfig
import codecheck.github.models.WebhookCreateInput
import codecheck.github.models.WebhookUpdateInput


class WebhookOpSpec extends FunSpec with Constants with BeforeAndAfter {

  val targetURL = "http://github-hook.herokuapp.com/hook"
  var nID: Long = 0;

  describe("listWebhooks(owner, repo)") {
    it("should succeed with valid owner, repo.") {
      val result = Await.result(api.listWebhooks(user, userRepo), TIMEOUT)
      assert(result.length >= 0)
    }
  }

  describe("createWebhook(owner, repo, input)") {
    it("should succeed with valid user, repo, and inputs.") {
      val config = WebhookConfig(targetURL)
      val input = WebhookCreateInput("web", config, events=Seq("*"))
      val res = Await.result(api.createWebhook(user, userRepo, input), TIMEOUT)
      showResponse(res)
      nID = res.id
      assert(res.url == "https://api.github.com/repos/" + user + "/" + userRepo + "/hooks/" + nID)
      assert(res.test_url == "https://api.github.com/repos/" + user + "/" + userRepo + "/hooks/" + nID + "/test")
      assert(res.ping_url == "https://api.github.com/repos/" + user + "/" + userRepo + "/hooks/" + nID + "/pings")
      assert(res.name == "web")
      assert(res.events == Seq("*"))
      assert(res.active == true)
      assert(res.config.url == Some(targetURL))
      assert(res.config.content_type == Some("json"))
      assert(res.config.secret == None)
      assert(res.config.insecure_ssl == Some(false))
    }
  }

  describe("getWebhook(owner, repo, id)") {
    it("should succeed with valid user, repo and id.") {
      Await.result(api.getWebhook(user, userRepo, nID), TIMEOUT).map { res =>
        assert(res.id == nID)
      }
    }
  }

  describe("updateWebhook(owner, repo, id, input)") {
    it("should succeed updating by rewriting events.") {
      val input = WebhookUpdateInput(events=Some(Seq("create", "pull_request")))
      val res = Await.result(api.updateWebhook(user, userRepo, nID, input), TIMEOUT)
      assert(res.events == Seq("create", "pull_request"))
    }

    it("should succeed updating by using add_events.") {
      val input = WebhookUpdateInput(add_events=Some(Seq("push")))
      val res = Await.result(api.updateWebhook(user, userRepo, nID, input), TIMEOUT)
      assert(res.config.url == Some(targetURL))
      assert(res.events == Seq("create", "pull_request", "push"))
    }

    it("should succeed updating by using remove_events.") {
      val input = WebhookUpdateInput(remove_events=Some(Seq("pull_request")))
      val res = Await.result(api.updateWebhook(user, userRepo, nID, input), TIMEOUT)
      assert(res.config.url == Some(targetURL))
      assert(res.events == Seq("create", "push"))
    }

    it("should succeed updating by rewriting config.") {
      val config = WebhookConfig(targetURL)
      val input = WebhookUpdateInput(Some(config))
      val res = Await.result(api.updateWebhook(user, userRepo, nID, input), TIMEOUT)
      assert(res.config.url == Some(targetURL))
    }
  }

  describe("testWebhook(owner, repo, id)") {
    it("should succeed with valid inputs.") {
      val result = Await.result(api.testWebhook(user, userRepo, nID), TIMEOUT)
      assert(result == true)
    }
  }

  describe("pingWebhook(owner, repo, id)") {
    it("should succeed with valid inputs.") {
      val result = Await.result(api.pingWebhook(user, userRepo, nID), TIMEOUT)
      assert(result == true)
    }
  }

  describe("removeWebhook(owner, repo, id)") {
    it("should succeed with valid inputs.") {
      val result = Await.result(api.removeWebhook(user, userRepo, nID), TIMEOUT)
      assert(result == true)
    }
  }
}
