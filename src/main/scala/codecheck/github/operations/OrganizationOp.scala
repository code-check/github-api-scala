package codecheck.github.operations

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import org.json4s.JArray

import codecheck.github.api.GitHubAPI
import codecheck.github.models.Organization
import codecheck.github.models.OrganizationDetail

trait OrganizationOp {
  self: GitHubAPI =>

  def getOrganizationList: Future[List[Organization]] = {
    self.exec("GET", s"/user/orgs").map { 
      _.body match {
        case JArray(arr) => arr.map(new Organization(_))
        case _ => throw new IllegalStateException()
      }
    }
  }

  def getOrganizationList(user: String): Future[List[Organization]] = {
    self.exec("GET", s"/users/${user}/orgs").map {
      _.body match {
        case JArray(arr) => arr.map(new Organization(_))
        case _ => throw new IllegalStateException()
      }
    }
  }

  def getOrganization(org: String): Future[OrganizationDetail] = {
    self.exec("GET", s"/orgs/${org}").map(result => new OrganizationDetail(result.body))
  }
}
