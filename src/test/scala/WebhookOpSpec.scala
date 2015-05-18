
import org.scalatest.FunSpec
import org.scalatest.BeforeAndAfter
import scala.concurrent.Await

import codecheck.github.models.Webhook
import codecheck.github.models.WebhookConfig
import codecheck.github.models.WebhookInput

class WebhookOpSpec extends FunSpec with Constants with BeforeAndAfter {

	val gURL = generateRandomWord;
	var nID: Long = 0;

	describe("listWebhooks(owner, repo)") {
	    it("should succeed with valid owner, repo.") {
	    	val result = Await.result(api.listWebhooks(organization, repo), TIMEOUT)
	  		//showResponse(result)
	    	assert(result.length > 0)
	    }
  	}

  	describe("createWebhook(owner, repo, input)") {
  		it("should succeed with valid inputs.") {
  			val config = new WebhookConfig("testsite.com/" + gURL)
  			val input = new WebhookInput("web", config, events=Seq("*"))
  			Await.result(api.createWebhook(organization, repo, input), TIMEOUT).map { res =>
  				assert(res.events == Seq("*"))
  				nID = res.id
  			}
  		}
  	} 

  	describe("getWebhook(owner, repo, id)") {
  		it("should succeed with valid id.") {
  			Await.result(api.getWebhook(organization, repo, nID), TIMEOUT).map { res =>
	    		assert(res.id == nID)
	    		showResponse(res.events);
	    	}
  		}
  	}
  	
  	describe("updateWebhook(owner, repo, id, input)") {
  		it("should succeed with valid inputs.") {
  			val config = new WebhookConfig("testsite.com/" + gURL)
  			val input = new WebhookInput("web", config)
  			Await.result(api.updateWebhook(organization, repo, nID, input), TIMEOUT).map { res =>
  				assert(res.events == Seq("push"))
  				
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