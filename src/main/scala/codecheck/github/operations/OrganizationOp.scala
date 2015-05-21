package codecheck.github.operations

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import org.json4s.JArray

import codecheck.github.api.GitHubAPI
import codecheck.github.exceptions.NotFoundException
import codecheck.github.models.Organization
import codecheck.github.models.OrganizationDetail
import codecheck.github.models.OrganizationInput

trait OrganizationOp {
  self: GitHubAPI =>

  def listOwnOrganizations: Future[List[Organization]] = {
    self.exec("GET", s"/user/orgs").map { 
      _.body match {
        case JArray(arr) => arr.map(new Organization(_))
        case _ => throw new IllegalStateException()
      }
    }
  }

  def listUserOrganizations(user: String): Future[List[Organization]] = {
    self.exec("GET", s"/users/${user}/orgs").map {
      _.body match {
        case JArray(arr) => arr.map(new Organization(_))
        case _ => throw new IllegalStateException()
      }
    }
  }

  def getOrganization(org: String): Future[Option[OrganizationDetail]] = {
    self.exec("GET", s"/orgs/${org}", fail404=false).map { res =>
      res.statusCode match {
        case 404 => None
        case 200 => Some(new OrganizationDetail(res.body))
      }
    }
  }

  def updateOrganization(org: String, input: OrganizationInput): Future[OrganizationDetail] = {
    self.exec("PATCH", s"/orgs/${org}", input.value).map { res => 
      new OrganizationDetail(res.body)
    }
  }
}