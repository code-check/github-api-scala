package codecheck.github.operations

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import org.json4s.JArray

import codecheck.github.api.GitHubAPI
import codecheck.github.exceptions.NotFoundException
import codecheck.github.models.Webhook
import codecheck.github.models.WebhookConfig
import codecheck.github.models.WebhookCreateInput
import codecheck.github.models.WebhookInput

trait WebhookOp {
  self: GitHubAPI =>

  def listWebhooks(owner: String, repo: String): Future[List[Webhook]] = {
    self.exec("GET", s"/repos/${owner}/${repo}/hooks").map { 
      _.body match {
        case JArray(arr) => arr.map(new Webhook(_))
        case _ => throw new IllegalStateException()
      }
    }
  }

  def getWebhook(owner: String, repo: String, id: Long): Future[Option[Webhook]] = {
    self.exec("GET", s"/repos/${owner}/${repo}/hooks/${id}", fail404=false).map { res =>
      res.statusCode match {
        case 404 => None
        case 200 => Some(new Webhook(res.body))
      }
    }
  } 

  def createWebhook(owner: String, repo: String, input: WebhookCreateInput): Future[Webhook] = {
    self.exec("POST", s"/repos/${owner}/${repo}/hooks", input.value).map { res =>
      new Webhook(res.body)
    }
  }

  //It is apparently an issue with Github's Webhook API that add_events and remove_events cannot be done
  //in a single operation. To add and remove events, it must be done through two seperate calls of updateWebhook.
  def updateWebhook(owner: String, repo: String, id: Long, input: WebhookInput): Future[Webhook] = {
    self.exec("PATCH", s"/repos/${owner}/${repo}/hooks/${id}", input.value).map { res =>
      new Webhook(res.body)
    }
  } 

  def testWebhook(owner: String, repo: String, id: Long): Future[Boolean] = {
    self.exec("POST", s"/repos/${owner}/${repo}/hooks/${id}/test", fail404=false).map { res =>
      res.statusCode match {
        case 204 => true
        case _ => false
      }
    }
  } 

  def pingWebhook(owner: String, repo: String, id: Long): Future[Boolean] = {
    self.exec("POST", s"/repos/${owner}/${repo}/hooks/${id}/pings", fail404=false).map { res =>
      res.statusCode match {
        case 204 => true
        case _ => false
      }
    }
  } 

  def removeWebhook(owner: String, repo: String, id: Long): Future[Boolean] = {
    self.exec("DELETE", s"/repos/${owner}/${repo}/hooks/${id}", fail404=false).map { res =>
      res.statusCode match {
        case 204 => true
        case _ => false
      }
    }
  } 
}
